# Aqua 💧

تطبيق أندرويد لتتبع شرب الماء اليومي — بتصميم بسيط وراقٍ، خلفية سوداء نقية ولون برتقالي مميز، مع دعم كامل للعربية والإنجليزية.

An Android app for tracking daily water intake — minimal, black-and-orange, fully bilingual (Arabic / English).

---

## ✨ الميزات / Features

- **تحديد هدف يومي** بالمل مع إمكانية التعديل في أي وقت
- **تسجيل سريع**: أزرار 250 مل و 500 مل، بالإضافة لإدخال كمية مخصصة
- **دائرة تقدم متحركة** تعرض النسبة من الهدف اليومي
- **سجل اليوم** مع إمكانية حذف أي إدخال خاطئ
- **إحصائيات** أسبوعية وشهرية برسم بياني + معدل وأفضل يوم
- **تذكيرات دورية** تعمل حتى والتطبيق مغلق، مع تحديد الفترة ونطاق الساعات
- **عربي / إنجليزي** مع تبديل فوري ودعم RTL / LTR تلقائي
- **Dark / Light Mode** مع خيار اتباع النظام
- **تخصيص اللون المميز** من 8 ألوان جاهزة
- **تغيير أيقونة التطبيق** بين 3 أيقونات (قطرة / موجة / حلقة)
- **صفحة حول** تتضمن اسم المطور والآية الكريمة

## 🛠 التقنيات / Tech stack

| الطبقة | التقنية |
|---|---|
| اللغة | Kotlin |
| الواجهة | Jetpack Compose + Material 3 |
| قاعدة البيانات | Room |
| الإعدادات | DataStore Preferences |
| الجدولة | WorkManager |
| التنقل | Navigation Compose |
| البناء | Gradle 8.7 + AGP 8.5.2 |

- **minSdk**: 26 (Android 8.0)
- **targetSdk / compileSdk**: 34

## 🚀 بناء ملف APK عبر GitHub Actions

لا تحتاج أي بيئة تطوير محلية. فقط:

1. أنشئ مستودعًا جديدًا على GitHub.
2. ارفع محتويات هذا المجلد إليه:

```bash
git init
git add .
git commit -m "Aqua: initial commit"
git branch -M main
git remote add origin https://github.com/<username>/Aqua.git
git push -u origin main
```

3. افتح تبويب **Actions** في المستودع — سيبدأ سير العمل `Build APK` تلقائيًا.
4. بعد انتهاء البناء، نزّل ملف الـ APK من قسم **Artifacts**:
   - `Aqua-debug-apk` — جاهز للتثبيت مباشرة على الهاتف.
   - `Aqua-release-unsigned-apk` — نسخة release غير موقّعة (تحتاج توقيع قبل النشر على Google Play).

سير العمل يعمل عند كل `push` وعند نشر أي `release`، ويمكن تشغيله يدويًا من زر **Run workflow**.

### توقيع نسخة الـ Release (اختياري)

لنشر التطبيق على Google Play تحتاج توقيع النسخة. أنشئ keystore، ثم أضفه كـ secrets في المستودع (`KEYSTORE_BASE64`، `KEYSTORE_PASSWORD`، `KEY_ALIAS`، `KEY_PASSWORD`) وأضف إعدادات `signingConfigs` في `app/build.gradle.kts`.

## 📁 بنية المشروع

```
app/src/main/java/com/alihalim/aqua/
├── AquaApplication.kt        # قناة الإشعارات + الـ repositories
├── MainActivity.kt           # نقطة الدخول + تبديل اللغة
├── data/
│   ├── SettingsRepository.kt # DataStore: اللغة، الثيم، اللون، الهدف، التذكيرات
│   ├── WaterRepository.kt    # منطق الشرب والحسابات اليومية
│   └── local/                # Room: Entity + DAO + Database
├── reminder/
│   ├── ReminderScheduler.kt  # جدولة التذكير التالي ضمن نطاق الساعات
│   ├── ReminderWorker.kt     # إرسال الإشعار وإعادة الجدولة
│   └── BootReceiver.kt       # إعادة الجدولة بعد إعادة التشغيل
└── ui/
    ├── AquaViewModel.kt
    ├── theme/                # الألوان، الخطوط، الثيم
    ├── components/           # دائرة التقدم، الرسم البياني، عناصر مشتركة
    ├── navigation/
    └── screens/              # Home / Stats / Settings / About
```

## 👤 المطور

**Ali Halim**

> «وَجَعَلْنَا مِنَ الْمَاءِ كُلَّ شَيْءٍ حَيٍّ»
