# Transaction Service Client Architecture

**Audience:** Payzone Transaction Service Client maintainers, reviewers, and engineers creating follow-on Jira stories.  
**Source:** Distilled primarily from `AT150E - Transaction Services - SUMMARY V1 - 2026-02-02 - 70% - PROD READY WITH CAVEATS.md` and the repo-local `doc/analysis.md`.  
**Scope:** Architecture and planning reference only. This document does not define a production-code change.

---

## 1. Purpose

The Transaction Service Android Client is an Android library used by consumer applications to communicate with the Payzone Transaction Service application. Its main job is to hide Android IPC details behind a small SDK-style API while supporting transaction lifecycle operations, Talexus hardware operations, Quantum/card flows, ePay operations, basket operations, and keypad/barcode-related requests.

This document turns the AT150E architecture findings into a stable internal reference that can be linked from Jira tickets and used when creating refactor, quality, and modernization stories.

---

## 2. Current Architecture Overview

At runtime, the client sits inside the consumer application and talks to the separate Payzone Transaction Service application using Android `Messenger` / `Handler` IPC. The service package is explicitly targeted as `com.payzone.transaction`, with package visibility declared in `src/main/AndroidManifest.xml`.

```text
Consumer app
  Activity / Fragment / ViewModel
        |
        | creates Messenger backed by a response Handler
        v
  ApiClient facade
        |
        | binds to explicit TransactionService component
        v
  Android Messenger IPC
        |
        v
Payzone Transaction Service app
  com.payzone.transaction.services.TransactionService
        |
        | compressed async responses via reply Messenger
        v
Consumer response Handler

Talexus hardware status is also surfaced via Android broadcasts:
  - talexus.key.inserted
  - talexus.box.connected
```

### Key Components

| Component | Current role |
| --- | --- |
| `ApiClient` | Public SDK entry point / facade. Exposes transaction, Talexus, Quantum, basket, keypad, ePay, token, merchant, and barcode methods. It converts caller input into protocol requests and delegates binding/sending work. |
| `MessageConstants` | Central protocol definition for request codes, response keys, internal bundle keys, and broadcast action names. |
| `ServiceConnectionManager` | Owns application context, explicit service binding/unbinding, service connection state, Talexus broadcast receiver registration, and compatibility handling for `RECEIVER_NOT_EXPORTED` on Android 13+. |
| `MessageSender` | Builds `Message`/`Bundle` payloads, waits for service binding asynchronously, sends requests through `Messenger`, and reports send failures back through the reply messenger. |
| `MessageResponseHandler` | Default response `Handler` using the main looper. Decompresses service responses and stores the latest response string for simple consumers/prototyping. Production consumers normally provide their own handler. |
| `CompressionUtils` | Handles Base64 + GZIP response decompression, including compatibility with the service response prefix skipped before GZIP decoding. |

> AT150E described the legacy baseline as a Java `ApiClient` god class. The current Kotlin source already reflects some future-state decomposition (`ServiceConnectionManager`, `MessageSender`, `CompressionUtils`). The architectural direction below remains the target: keep `ApiClient` as a thin facade and continue extracting infrastructure, policy, and domain-specific responsibilities behind injectable boundaries.

---

## 3. IPC Request / Response Flow

1. The consumer creates an `ApiClient`, usually with an application `Context` and a `Messenger` backed by a custom response `Handler`.
2. The consumer calls `initService()`.
3. The client binds to the explicit service component:
   - package: `com.payzone.transaction`
   - service: `com.payzone.transaction.services.TransactionService`
4. After binding, the client sends a config setup request and marks the service as available.
5. For each public API call, the client:
   - validates required arguments,
   - maps the method to a `MessageConstants.MSG_*` request code,
   - chooses the matching `MessageConstants.RESP_*` response key,
   - serializes input as a string payload, usually JSON,
   - includes the caller package name in the bundle,
   - sends the message through Android `Messenger`.
6. The Transaction Service processes the request and replies asynchronously to `Message.replyTo`.
7. The response handler reads the response bundle entry, decompresses the Base64/GZIP payload, and hands the resulting JSON/string response to application-specific code.
8. Talexus hardware state changes can arrive independently via broadcasts for key insertion and box connection state.

Important API behavior: public methods return a `Boolean` indicating whether the send operation was accepted/queued by the client path, not whether the transaction itself succeeded. Business success or failure is determined later from the asynchronous service response.

---

## 4. `ApiClient` Role and Responsibility Boundary

`ApiClient` should be treated as the public facade for the SDK. Its stable responsibilities are:

- expose the public API methods used by consumer applications;
- preserve protocol compatibility with existing request codes and response keys;
- perform lightweight parameter validation and JSON wrapping where required by the service protocol;
- delegate service binding, message delivery, compression, retry/timeout policy, and hardware monitoring to focused collaborators;
- keep backward-compatible static helpers such as response decompression only where needed for existing consumers.

Responsibilities that should not accumulate in `ApiClient`:

- direct lifecycle management of bound services beyond facade calls;
- low-level `Message` and `Bundle` construction details;
- retry, timeout, and backoff policy;
- broadcast receiver implementation details;
- response parsing/decompression implementation;
- domain-specific typed request/response mapping.

Keeping this boundary clear prevents the class from regressing into the god-class shape identified in AT150E.

---

## 5. Architectural Pain Points and Risks from AT150E

AT150E identified the following structural issues and risks. Some have already been partially addressed in the current codebase, but they remain useful as guardrails for future stories.

| Pain point / risk | Why it matters | Architectural implication |
| --- | --- | --- |
| God class anti-pattern | The legacy `ApiClient` mixed service binding, message construction, retry logic, decompression, broadcast receivers, and 30+ API methods. | Keep `ApiClient` thin. New responsibilities should go into focused collaborators. |
| Tight coupling to Android IPC | Business API methods were directly coupled to `Handler`, `Messenger`, `Bundle`, `Context`, and broadcast implementation details. | Introduce interfaces/adapters so message sending and connection state can be tested without Android framework dependencies. |
| Weak testability | Local tests struggled to exercise service binding, IPC, decompression, timeout behavior, and broadcasts. | Move logic into pure or mockable units; add fake senders/managers and decompression tests. |
| Misleading synchronous return values | `Boolean` results can be mistaken for business success even though responses are asynchronous. | Document this behavior now; consider future callback/result abstractions or typed async APIs. |
| Error handling risk | Silent failures and blocking retry paths can hide failed payment-state updates. | Centralize failure reporting and make send failure observable to consumers. |
| Lifecycle and memory risks | Holding activity contexts or registering receivers without clean lifecycle boundaries can leak UI objects. | Use application context for long-lived components and make bind/unbind ownership explicit. |
| Scalability of protocol handling | Adding more message types increases duplication in request wrappers and response handler switch/when logic. | Move toward request descriptors, typed models, and generated or table-driven routing. |
| Security-sensitive IPC surface | Service binding and broadcasts are part of a payment flow. Spoofing or binding to an unexpected service would be high impact. | Keep explicit component targeting; continue strengthening signature checks, permissions, and receiver export controls. |

---

## 6. Recommended Future-State Architecture

AT150E recommended decomposing the client into a facade plus focused collaborators. The goal is a DI-ready design where Android framework integration is isolated and business-facing API behavior is easy to unit test.

```text
ApiClient facade
  |
  +-- ServiceConnectionManager
  |     - bind / unbind
  |     - connection state
  |     - service identity checks
  |
  +-- MessageSender
  |     - message construction
  |     - async send
  |     - timeout / retry policy
  |     - send failure reporting
  |
  +-- TalexusHardwareMonitor
  |     - key inserted events
  |     - box connected events
  |     - receiver registration policy
  |
  +-- CompressionUtils / ResponseCodec
  |     - Base64 + GZIP decompression
  |     - size limits and malformed payload handling
  |
  +-- Protocol / Request descriptors
        - request code
        - response key
        - payload wrapper
        - typed model mapping
```

### Target Design Principles

- **Facade first:** Consumers continue to depend on `ApiClient`, not internal implementation classes.
- **Single responsibility collaborators:** Service connection, sending, hardware monitoring, compression, and protocol mapping evolve independently.
- **DI-ready construction:** Internal collaborators should be injectable or replaceable in tests without requiring a real Android service.
- **Lifecycle clarity:** Bind/unbind and receiver registration should be explicit, idempotent, and safe across activity/fragment lifecycle changes.
- **Observable failures:** Send failures, service timeouts, and service disconnections should be propagated through a documented error path.
- **Protocol compatibility:** Existing `MessageConstants` values remain stable unless a major-version migration is planned.
- **Typed evolution path:** JSON string methods can remain for compatibility, while typed request/response models are introduced additively.

### DI/Testability Alignment

Future Jira stories should aim to make these seams testable:

- replace `ServiceConnectionManager` with a fake connection state provider;
- replace `MessageSender` with a fake sender that records request descriptors;
- test protocol mapping without Android `Messenger` or `Bundle`;
- test decompression with normal, malformed, empty, oversized, and compatibility-prefixed payloads;
- test hardware monitoring with an injectable broadcast/event adapter;
- test response handling without logging or stdout side effects.

---

## 7. Roadmap Mapping for Jira Planning

The AT150E strategic roadmap can be translated into Jira epics/stories using the following grouping.

### Near-term refactors / stabilization

Purpose: reduce payment-flow risk while preserving the existing public API.

Candidate story themes:

- Keep `ApiClient` as a thin facade and prevent new infrastructure logic from being added directly to it.
- Centralize send failure behavior and document how consumers observe failed sends.
- Finish extracting retry/backoff policy from message delivery if retry behavior is reintroduced or changed.
- Harden lifecycle behavior for service binding/unbinding and broadcast receiver registration.
- Add service identity validation beyond explicit component targeting where feasible.
- Add or maintain consumer ProGuard/R8 guidance for public API and handlers.

### Medium-term quality improvements

Purpose: make the client easier to test, maintain, and safely extend.

Candidate story themes:

- Add focused unit tests for `MessageSender`, `ServiceConnectionManager`, `CompressionUtils`, and protocol mapping.
- Add integration-style tests or fakes for Messenger request/response flows.
- Introduce a request descriptor abstraction to reduce duplicated wrapper logic.
- Split Talexus broadcast handling into a dedicated `TalexusHardwareMonitor` or event adapter.
- Add code coverage gates/reporting and ensure IPC/decompression paths are covered.
- Improve default response handling so all request codes have predictable response/error behavior.

### Longer-term modernization

Purpose: provide a modern SDK surface while keeping Java consumers supported.

Candidate story themes:

- Introduce typed request and response models alongside existing `JSONObject` APIs.
- Add coroutine/suspend or callback/result-based APIs that make asynchronous success/failure explicit.
- Consider Flow-style event streams for hardware and service state.
- Formalize dependency injection for internal collaborators.
- Continue Android SDK/Gradle/Kotlin modernization and compatibility work.
- Evaluate stronger IPC security, including service signature verification and permission-protected broadcasts where compatible with the Transaction Service app.

---

## 8. Reference Checklist for Future Stories

When creating or refining architecture stories, check that the story states:

- which responsibility is being moved or protected;
- whether the public `ApiClient` API changes or remains backward compatible;
- how asynchronous send failure and business response success/failure are represented;
- which Android framework dependency is being isolated or faked;
- what tests will cover the new seam;
- whether protocol constants or wire-format compatibility are affected;
- whether the change affects service binding, broadcasts, or payment-sensitive data handling.

---

## 9. Source References

- Full AT150E analysis: `AT150E - Transaction Services - SUMMARY V1 - 2026-02-02 - 70% - PROD READY WITH CAVEATS.md`
- Client facade: `src/main/java/com/payzone/transaction/client/ApiClient.kt`
- Service binding and broadcasts: `src/main/java/com/payzone/transaction/client/ServiceConnectionManager.kt`
- Message delivery: `src/main/java/com/payzone/transaction/client/MessageSender.kt`
- Response decompression: `src/main/java/com/payzone/transaction/client/CompressionUtils.kt`
- Default response handler: `src/main/java/com/payzone/transaction/client/handlers/MessageResponseHandler.kt`
- Protocol constants: `src/main/java/com/payzone/transaction/client/MessageConstants.kt`
