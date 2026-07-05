# 🚀 FEATURE IMPLEMENTATION GUIDE

Mọi tính năng mới phải được triển khai theo 10 bước chuẩn hóa:

## 📁 Thư mục mẫu: `features/new_feature/`
├─ domain/
│   ├─ model/       # Domain Models
│   └─ usecase/     # Business Logic
├─ data/            # Repository Impl (if needed)
└─ presentation/
    ├─ model/       # 3-Level UiState, Event, Effect
    ├─ viewmodel/   # ViewModel
    ├─ mapper/      # UiMapper
    └─ screen/      # Compose Screens

## 📝 Các bước thực hiện:

1.  **Create Domain Model**: Định nghĩa dữ liệu nghiệp vụ thuần túy.
2.  **Create Repository Interface**: Khai báo các hàm cần thiết cho dữ liệu.
3.  **Create UseCase**: Thực thi logic cộng/trừ/lọc dữ liệu bằng `Long`.
4.  **Create UiState**: Định nghĩa 3 tầng: `ScreenState`, `ContentState`, `FilterState`.
5.  **Create Event**: Định nghĩa các hành động `OnAction`.
6.  **Create UiEffect**: Định nghĩa điều hướng hoặc thông báo.
7.  **Create ViewModel**: Điều phối luồng và emit 3 luồng State.
8.  **Create Mapper**: Chuyển đổi Domain Model sang UI Model (format String/Color).
9.  **Create Compose Screen**: Xây dựng giao diện "Dumb UI", chỉ collect state.
10. **Create Unit Tests**: Viết test cho UseCase và ViewModel (MVI Flow).
