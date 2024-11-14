# 各處理功能說明

透過 Java，模擬多種影像處理技術。
主要功能包括 灰階轉換、負片、Gamma 校正、二值化、胡椒鹽雜訊處理、中值濾波、Laplacian 邊緣檢測 及 最大濾波器

1. 灰階轉換
  透過 RGB 通道加權計算，將彩色影像轉換為灰階。
2. 負片
  將灰階影像進行反相處理：255 - 像素值。
3. Gamma 校正
  - Gamma > 1（暗部更暗）：增強暗部對比度。
  - Gamma < 1（亮部更亮）：提升暗部細節。
  - Gamma = 1：對比拉伸。
5. 二值化
  使用 OTSU 方法 計算閾值，將影像轉換為純黑或純白。
6. 胡椒鹽雜訊
  隨機在影像中生成黑白點，模擬雜訊干擾。
7. 3x3 中值濾波
  使用 中值濾波 去除胡椒鹽雜訊，保留邊緣細節。
8. Laplacian 邊緣檢測
  使用 Laplacian 運算元 檢測邊緣，強化圖像的輪廓。
9. 3x3 最大濾波
  使用 最大濾波器 增強影像亮部細節。