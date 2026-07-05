# 📜 MONEYFLOW DEVELOPMENT RULES

Đây là bộ quy tắc bắt buộc cho mọi thành viên tham gia dự án.

## ✅ Allowed (Được phép)
1.  ViewModel chỉ được inject UseCase hoặc Aggregator UseCases.
2.  UseCase phải nằm trong module `domain` và chỉ chứa logic `Long`.
3.  Dữ liệu hiển thị trên UI **bắt buộc** là String hoặc Int/Color đã qua xử lý.
4.  Sử dụng `MutableSharedFlow` cho các sự kiện điều hướng (Navigate).
5.  Mọi Class/Function mới phải có Unit Test nếu nằm trong module Critical.
6.  Sử dụng `AppRoute` để quản lý mọi đường dẫn Navigation.
7.  Dùng `MoneyParser.parse()` cho mọi input số tiền.

## ❌ Forbidden (Nghiêm cấm)
1.  **Cấm** sử dụng `Double/Float` để tính toán số dư hoặc số tiền.
2.  **Cấm** ViewModel truy cập trực tiếp vào DAO hoặc DataStore.
3.  **Cấm** viết logic định dạng ngày tháng/tiền tệ ngay trong `@Composable`.
4.  **Cấm** rò rỉ Entity (Database model) lên tầng UI.
5.  **Cấm** gọi `navController.navigate("route_string")` trực tiếp từ ViewModel.
6.  **Cấm** split StateFlow quá nhỏ (nhiều hơn 3 tầng quy định).
7.  **Cấm** sử dụng logic lọc (Filter/Search) bên trong Screen.
8.  **Cấm** Hard-coded các chuỗi String hiển thị (phải dùng `strings.xml`).
9.  **Cấm** bỏ qua việc xử lý lỗi (Error handling) trong UseCase.
10. **Cấm** sử dụng `GlobalScope` hoặc `MainScope` thủ công (dùng `viewModelScope`).
11. **Cấm** inject `Context` vào ViewModel hoặc UseCase.
12. **Cấm** tạo logic nghiệp vụ bên trong Mapper (Mapper chỉ để ánh xạ dữ liệu).
13. **Cấm** sử dụng `delay()` để fix các vấn đề bất đồng bộ (Race condition).
