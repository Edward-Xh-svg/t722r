# AchrafTube

تطبيق Android TV بسيط يفتح موقعك المحلي (`http://10.42.0.1:2222`) داخل WebView بملء الشاشة، بشكل شبيه بتطبيق يوتيوب، مخصص لمشاهدة الفيديوهات من سيرفر محلي بدون إنترنت.

## المميزات
- يفتح مباشرة على الموقع المحلي عند تشغيل التطبيق.
- يدعم تشغيل فيديو HTML5 بملء الشاشة (fullscreen) بسلاسة.
- تشغيل تلقائي للفيديو بدون الحاجة لضغطة تفعيل (autoplay).
- شاشة انتظار (loading) وشاشة خطأ بسيطة لو تعذر الاتصال بالسيرفر، مع إعادة محاولة بأي ضغطة زر من الريموت.
- يظهر في واجهة إطلاق Android TV (Leanback Launcher).

## قبل البناء
1. لو تغيّر عنوان أو منفذ السيرفر المحلي عندك، عدّل القيمة دي في الملف:
   `app/src/main/java/com/achraf/tube/MainActivity.java`
   ```java
   private static final String SERVER_URL = "http://10.42.0.1:2222";
   ```
   ولازم كمان تعدّل نفس العنوان (الـ IP بس بدون المنفذ) في:
   `app/src/main/res/xml/network_security_config.xml`

2. (اختياري) استبدل شعار وبانر التطبيق بصور حقيقية بدل placeholder:
   - `app/src/main/res/drawable/app_icon.xml`
   - `app/src/main/res/drawable/tv_banner.xml` (المقاس المفضّل لبانر TV هو 320×180)

## البناء عبر GitHub Actions
1. ارفع المشروع كامل (بالمجلدات والملفات زي ما هي) إلى مستودع GitHub.
2. مع كل `push`، هيشتغل الـ workflow الموجود في `.github/workflows/build.yml` تلقائيًا، وهيبني نسخة Gradle 8.7 مع AGP 8.4.0.
3. بعد ما ينجح البناء، هتلاقي ملف `app-debug.apk` تحت تبويب **Actions → آخر تشغيل → Artifacts**.

## التثبيت على جهاز Android TV
- حمّل ملف الـ APK من الـ Artifacts.
- ثبّته عن طريق تطبيق زي "Send Files to TV" أو USB أو ADB:
  ```
  adb install app-debug.apk
  ```
- تأكد إن جهاز الـ TV متصل بنفس الشبكة المحلية اللي عليها السيرفر (نفس نطاق الـ IP `10.42.0.1`).

## ملاحظة أمان
التطبيق مسموح فيه بالاتصال غير المشفر (`http`) فقط مع عنوان السيرفر المحلي المحدد، مش مع أي موقع تاني، عشان الأمان.
