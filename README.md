# ShoppingApp 🛒
 Pazarama Android Kotlin Bootcamp Bitirme Projesi
 
 [FAKE STORE API](https://fakestoreapi.com/) kullanılarak MVVM ve Clean Architecture ile oluşturulmuş Android alışveriş uygulaması.
 
## Uygulama Önizlemeleri
https://1drv.ms/i/s!As4moNPcWJRNgTXuLedoxgiN8wcG?e=erM3do

https://1drv.ms/i/s!As4moNPcWJRNgToMSc2V6IQfpx2x?e=6oGB3M

https://1drv.ms/i/s!As4moNPcWJRNgTwlr3VR3r8nbIGF?e=FQbNEx

https://1drv.ms/i/s!As4moNPcWJRNgTtSfJXOEnJoOICk?e=ywfEHM

https://1drv.ms/i/s!As4moNPcWJRNgT3jPwoH5iohntz7?e=gaKLI7

## Uygulama Ekran Görüntüleri

Splash Ekranı | İlk Onboarding Ekranı | İkinci Onboarding Ekranı | Üçüncü Onboarding Ekranı | Giriş Ekranı | Kayıt Ekranı
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
<img src="Read Me/Screenshot_1684275125.png" width="220"/> | <img src="Read Me/Screenshot_1684275153.png" width="140"/> | <img src="Read Me/Screenshot_1684275160.png" width="134"/> | <img src="Read Me/Screenshot_1684275164.png" width="114"/> | <img src="Read Me/Screenshot_1684275181.png" width="220"/> | <img src="Read Me/Screenshot_1684275190.png" width="220"/> |

Ürünler Ekranı | Ürün Detay Ekranı | Arama Ekranı | Alışveriş Sepeti Ürün Ekranı | Profil Ekranı |
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:|
<img src="Read Me/Screenshot_1684275434.png" width="144"/> | <img src="Read Me/Screenshot_1684275445.png" width="144"/> | <img src="Read Me/Screenshot_1684275753.png" width="144"/> | <img src="Read Me/Screenshot_1684276063.png" width="134"/> | <img src="Read Me/Screenshot_1684276096.png" width="144"/> |

## Teknoloji Yığını
- [Kotlin](https://developer.android.com/kotlin) - Kotlin, JVM üzerinde çalışabilen bir programlama dilidir. Google, Android Studio'da resmi olarak desteklenen programlama dillerinden biri olarak Kotlin'i duyurdu; ve Android topluluğu, Java'dan Kotlin'e büyük bir hızla geçiş yapıyor.
- Jetpack bileşenleri:
    - [Android KTX](https://developer.android.com/kotlin/ktx.html) - Android KTX, Android Jetpack ve diğer Android kitaplıklarına dahil olan bir Kotlin uzantısı grubudur. KTX uzantıları; Jetpack, Android platformu ve diğer API'lere kısa ve öz deyimler sunan Kotlin sağlar.
    - [AndroidX](https://developer.android.com/jetpack/androidx) - Artık korunmayan orijinal Android ([Support Library](https://developer.android.com/topic/libraries/support-library/index)) Destek Kitaplığı'nda büyük gelişme.
    - [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - Yaşam döngüsüne duyarlı bileşenler, etkinlikler ve parçalar gibi başka bir bileşenin yaşam döngüsü durumundaki bir değişikliğe yanıt olarak eylemler gerçekleştirir. Bu bileşenler, bakımı daha kolay olan daha iyi organize edilmiş ve genellikle daha hafif kodlar üretmenize yardımcı olur.
    - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - ViewModel sınıfı, UI ile ilgili verileri yaşam döngüsüne duyarlı bir şekilde depolamak ve yönetmek için tasarlanmıştır.
	- [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) - Uygulama içi navigasyon için gereken her şeyi halledin. optimum yürütme için eşzamansız görevler.
	- [ViewPager2](https://developer.android.com/jetpack/androidx/releases/viewpager2) - ViewPager2, androidx.viewpager.widget.ViewPager'ın yerini alarak sağdan sola düzen desteği, dikey yönlendirme, değiştirilebilir Fragment koleksiyonları vb. dahil olmak üzere selefinin sıkıntılı noktalarının çoğunu ele alıyor.

- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines) - Eşzamansız olarak yürütülen kodu basitleştirmek için Android'de kullanabileceğiniz bir eşzamanlılık tasarım modeli.
- [Retrofit](https://square.github.io/retrofit) -  Retrofit, Apache 2.0 lisansı altında Square inc tarafından Java/ Kotlin ve Android için bir REST istemcisidir. Ağ işlemleri için kullanılan basit bir ağ kitaplığıdır. Bu kitaplığı kullanarak, web hizmetinden/web API'sinden JSON yanıtını sorunsuz bir şekilde yakalayabiliriz.
- [Kotlin Flow](https://developer.android.com/kotlin/flow) - Eşzamanlılarda akış, yalnızca tek bir değer döndüren askıya alma işlevlerinin aksine sırayla birden çok değer yayan bir türdür. Örneğin, bir veritabanından canlı güncellemeler almak için bir akış kullanabilirsiniz.
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - Android için, projenizde manuel dependency injection yapma şablonunu azaltan bir dependency injection kitaplığı.
- [Logging Interceptor](https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/README.md) - HTTP isteğini ve yanıt verilerini günlüğe kaydeder.
- [Glide](https://github.com/bumptech/glide)- Android için Kotlin Coroutines tarafından desteklenen bir resim yükleme kitaplığı.
- [Firebase Authentication](https://firebase.google.com/docs/auth) - Firebase Authentication, uygulamanızda kullanıcıların kimliğini doğrulamak için arka uç hizmetleri, kullanımı kolay SDK'lar ve hazır kullanıcı arabirimi kitaplıkları sağlar. Parolalar, telefon numaraları, Google, Facebook ve Twitter gibi popüler birleştirilmiş kimlik sağlayıcıları ve daha fazlasını kullanarak kimlik doğrulamayı destekler.
- [Firebase Cloud Firestore](https://firebase.google.com/docs/firestore) - Cloud Firestore, Firebase ve Google Cloud'dan mobil, web ve sunucu geliştirme için esnek, ölçeklenebilir bir veritabanıdır.
- [Lottie](https://lottiefiles.com/)-  LottieFiles, Motion Design'ın karmaşıklığını ortadan kaldırır. Mümkün olan en kolay şekilde bir Lottie Oluşturmanıza, Düzenlemenize, Test Etmenize, İşbirliği Yapmanıza ve Sevk Etmenize olanak tanır.
- [Swiperefreshlayout](https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout) - UI modelini yenilemek için kaydırmayı uygulayın.

## Teşekkürler
- [Pazarama Ekibi](https://www.pazarama.com/) bu Bootcamp'i açtığı için.
- [Patika.Dev](https://www.patika.dev/tr) bu eğitim organizasyonunu oluşturduğu için.
- [@MertToptas](https://github.com/merttoptas) 7 haftalık kotlin eğitimi için.
- [Bootcamp Ekibim](https://github.com/Pazarama-Android-Kotlin-Bootcamp) iyi bir ekip üyesi olduğu için.

