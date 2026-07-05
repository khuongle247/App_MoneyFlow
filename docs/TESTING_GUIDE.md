# 🧪 TESTING GUIDE

## 🛠️ Công cụ sử dụng
*   **MockK**: Để giả lập (Mock) các Repository và UseCase.
*   **Turbine**: Để kiểm thử các luồng `Flow` và `StateFlow`.
*   **Coroutines Test**: Sử dụng `runTest` và `MainDispatcherRule`.

## 📜 Naming Conventions
Tên hàm test phải mô tả rõ kịch bản:
`should [ExpectedResult] when [Action/Input]`
*Ví dụ:* `shouldUpdateFilteredList when SearchQueryChanged`

## 🎯 Chiến lược kiểm thử

### 1. Critical Module Testing (Transaction, Statistics, Budget)
*   **Yêu cầu**: Phủ 90-100% logic.
*   **Trọng tâm**: 
    *   Tính chính xác của phép toán `Long`.
    *   Sự nhất quán của `ContentState` khi `FilterState` thay đổi.
    *   Verify các `UiEffect` quan trọng (NavigateBack).

### 2. Right-Sized Testing (Saving Goal, Onboarding)
*   **Yêu cầu**: Phủ các luồng điều hướng chính.
*   **Trọng tâm**: 
    *   Kiểm tra logic Splash Screen điều hướng đúng user cũ/mới.
    *   Kiểm tra Mapper tính toán % tiến độ cơ bản.

## 💡 Ví dụ Verify Effect (Turbine)
```kotlin
viewModel.effect.test {
    viewModel.onEvent(TransactionEvent.OnSave)
    assertEquals(TransactionEffect.NavigateBack, awaitItem())
}
```
