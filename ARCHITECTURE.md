# 🏛️ MONEYFLOW ARCHITECTURE

## Project Overview
**MoneyFlow** là một ứng dụng quản lý tài chính cá nhân chuẩn Fintech, tập trung vào tính chính xác tuyệt đối của dữ liệu và trải nghiệm người dùng mượt mà thông qua Jetpack Compose. Dự án được thiết kế để mở rộng (Scalable) và dễ bảo trì (Maintainable) bằng cách áp dụng các mô hình kiến trúc tiên tiến nhất trong thế giới Android.

## Architectural Principles
Hệ thống tuân thủ 4 nguyên tắc cốt lõi:
1.  **Clean Architecture**: Tách biệt mã nguồn thành các lớp độc lập.
2.  **Dependency Rule**: Sự phụ thuộc chỉ hướng vào bên trong (Presentation -> Domain <- Data).
3.  **Separation of Concerns**: Mỗi thành phần chỉ đảm nhận một trách nhiệm duy nhất.
4.  **Single Source of Truth (SSOT)**: Dữ liệu tài chính chỉ được quản lý tại tầng Data (Room/DataStore).

## Layer Structure

### Data Layer
*   **Trách nhiệm**: Quản lý Persistence (Room Database), Key-value storage (DataStore) và thực thi các Repository.
*   **Dependencies**: Room, DataStore, Hilt.
*   **Cấm**: Tuyệt đối không chứa logic nghiệp vụ tài chính (cộng/trừ, tính % ngân sách).

### Domain Layer
*   **Trách nhiệm**: Chứa các UseCase và Domain Model. Đây là lớp quan trọng nhất, chứa các quy tắc nghiệp vụ (Business Rules).
*   **UseCases**: Mỗi hành động của người dùng tương ứng với một UseCase (AddTransaction, GetSummary...).
*   **Quy tắc**: Phải là **Pure Kotlin**. Không phụ thuộc vào Android Framework hay UI.

### Presentation Layer
*   **Trách nhiệm**: Quản lý UI State, xử lý sự kiện người dùng (MVI) và hiển thị giao diện.
*   **ViewModels**: Điều phối dữ liệu từ UseCase và đóng gói vào 3-Level State.
*   **UiState/Effect**: Trạng thái và các hiệu ứng một lần (side-effects).
*   **Mappers**: Chuyển đổi dữ liệu từ Domain Model sang UI Model (đã format sẵn).

## Feature Structure
Dự án được tổ chức theo tính năng (**Feature-Based**):
features/
 ├─ dashboard    # Tổng quan tài chính
 ├─ transaction  # Lịch sử & Thêm giao dịch
 ├─ budget       # Quản lý phong bì chi tiêu
 ├─ statistics   # Biểu đồ & Báo cáo
 ├─ settings     # Cấu hình & Backup
 └─ onboarding   # Splash & Khởi tạo user

## Data Flow
`Repository` (Raw Data) ➔ `UseCase` (Logic/Calculation) ➔ `ViewModel` (Orchestration) ➔ `Mapper` (Formatting) ➔ `UiState` ➔ `Compose Screen`.

## MVI Flow
1.  **Intent**: UI gửi `TransactionEvent.OnSave` tới ViewModel.
2.  **Process**: ViewModel gọi UseCase phù hợp.
3.  **State**: ViewModel cập nhật `UiState` (Level 1, 2 hoặc 3).
4.  **Effect**: Nếu cần điều hướng, ViewModel phát ra `UiEffect.NavigateBack`.
5.  **Render**: UI quan sát State/Effect và vẽ lại giao diện.

## Money System
*   **Long-based**: Mọi giá trị tiền tệ được lưu dưới dạng `Long` (đơn vị VND nhỏ nhất) để tránh sai số dấu phẩy động của `Double/Float`.
*   **MoneyParser**: Bộ phân tách chuỗi nhập từ người dùng sang `Long` chuẩn xác.
*   **Formatter**: Chỉ hiển thị chuỗi tiền tệ (String) ở lớp Presentation sau khi đã qua Mapper.

## Architecture Lock Rules
*   Không sử dụng `Double` cho tiền tệ.
*   Không gọi Repository từ UI.
*   Mọi màn hình phải dùng **3-Level State Model**.
*   ViewModel không được biết về `NavController`.
