package com.app.road.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.app.road.R
import com.app.road.Repository
import com.app.road.domain.Course
import com.app.road.log
import com.app.road.model.Author
import com.app.road.v4.Utils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.tinkoff.acquiring.sdk.AcquiringSdk
import ru.tinkoff.acquiring.sdk.TinkoffAcquiring
import ru.tinkoff.acquiring.sdk.localization.AsdkSource
import ru.tinkoff.acquiring.sdk.localization.Language
import ru.tinkoff.acquiring.sdk.models.DarkThemeMode
import ru.tinkoff.acquiring.sdk.models.enums.CheckType
import ru.tinkoff.acquiring.sdk.models.options.screen.PaymentOptions
import ru.tinkoff.acquiring.sdk.utils.Money

class SubscribeBaseActivity : AppCompatActivity() {

    val REQUEST = 7788
    //Terminal key
    val TERMINAL_KEY = "1653643392741"
    //Password
    val PASSWORD = "frdu7q04dit5bip4"
    //Public key
    val PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv5yse9ka3ZQE0feuGtemYv3IqOlLck8zHUM7lTr0za6lXTszRSXfUO7jMb+L5C7e2QNFs+7sIX2OQJ6a+HG8kr+jwJ4tS3cVsWtd9NXpsU40PE4MeNr5RqiNXjcDxA+L4OsEm/BlyFOEOh2epGyYUd5/iO3OiQFRNicomT2saQYAeqIwuELPs1XpLk9HLx5qPbm8fRrQhjeUD5TLO8b+4yCnObe8vy/BMUwBfq+ieWADIjwWCMp2KTpMGLz48qnaD9kdrYJ0iyHqzb2mkDhdIzkim24A3lWoYitJCBrrB2xM05sm9+OdCI1f7nPNJbl5URHobSwR94IRGT7CJcUjvwIDAQAB"

    var authorName = ""
    var author: Author? = null
    var course: String? = null
    var priceInCents: Long = 2500

    private fun payMonth(){
        val auth = Firebase.auth
        try {
            val tinkoffAcquiring = TinkoffAcquiring(TERMINAL_KEY, PUBLIC_KEY)
            val courseName = if(course.isNullOrEmpty()) "Покупка курса" else "Покупка курса $course"
            var paymentOptions = PaymentOptions().setOptions {
                orderOptions { // данные заказа
                    orderId = auth.uid!! + courseName.filter { !it.isWhitespace() }
                    amount = Money.ofCoins(priceInCents)
                    title = courseName
                    description = authorName
                    recurrentPayment = false
                }
                customerOptions { // данные покупателя
                    customerKey = auth.uid
                    email = auth.currentUser!!.email
                    checkType = CheckType.NO.toString()
                }
                featuresOptions { // настройки визуального отображения и функций экрана оплаты
                    useSecureKeyboard = true
                    localizationSource = AsdkSource(Language.RU)
                    handleCardListErrorInSdk = true
                    darkThemeMode = DarkThemeMode.AUTO
                }
            }
            tinkoffAcquiring.openPaymentScreen(this, paymentOptions, REQUEST)
        } catch (e: Exception){
            Toast.makeText(this,"Покупка невозможна", Toast.LENGTH_SHORT).show()
        }
    }

    private fun payYear(){
        val auth = Firebase.auth
        try {
            val tinkoffAcquiring = TinkoffAcquiring(TERMINAL_KEY, PUBLIC_KEY)
            val courseName = if(course.isNullOrEmpty()) "Покупка курса" else "Покупка курса $course"
            var paymentOptions = PaymentOptions().setOptions {
                orderOptions { // данные заказа
                    orderId = auth.uid!! + courseName.filter { !it.isWhitespace() }
                    amount = Money.ofCoins(199000)
                    title = "Курс $authorName"
                    description = "Покупка курса на год"
                    recurrentPayment = false
                }
                customerOptions { // данные покупателя
                    customerKey = auth.uid
                    email = auth.currentUser!!.email
                    checkType = CheckType.NO.toString()
                }
                featuresOptions { // настройки визуального отображения и функций экрана оплаты
                    useSecureKeyboard = true
                    localizationSource = AsdkSource(Language.RU)
                    handleCardListErrorInSdk = true
                    darkThemeMode = DarkThemeMode.AUTO
                }
            }
            tinkoffAcquiring.openPaymentScreen(this, paymentOptions, REQUEST)
        } catch (e: Exception){
            Toast.makeText(this,"Покупка невозможна", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST && resultCode == Activity.RESULT_OK) {
            Repository.trial = false
            Repository.premium = true
            Repository.millionPremium = true
            val db = Firebase.firestore
            val auth = Firebase.auth
            val uid = auth.currentUser!!.uid
            val user = hashMapOf(
                "trial" to false,
                "premium" to true,
                "million_premium" to true,
            )
            db.collection("users")
                .document(uid)
                .update(user as Map<String, Any>)
                .addOnCompleteListener {
                    Toast.makeText(this,"Спасибо за покупку", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, BaseActivity::class.java))
                    finish()
                }

        } else if(requestCode == REQUEST && resultCode == TinkoffAcquiring.RESULT_ERROR){
            Toast.makeText(this,"Оплата не прошла", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe_base)
        val month = findViewById<View>(R.id.month)
        val year = findViewById<View>(R.id.year)
        val trial = findViewById<View>(R.id.trial)

        val tvPriceWithoutDiscount = findViewById<TextView>(R.id.tvPriceWithoutDiscount)

        val intentAuthorName = when(intent.getStringExtra("author")) {
            Author.VASILIY.name -> Author.VASILIY
            Author.ELENA.name -> Author.ELENA
            Author.MILLION.name -> Author.MILLION
            "Екатерина" -> "Екатерина"
            "Медитации" -> "Медитации"
            else -> null
        }

        val courseTitle = when(intent.getStringExtra("course_name")) {
            Course.MONEY_IN_THE_HEAD.name -> "Деньги в голове"
            Course.BODY_AND_MENTAL.name -> "Тело и психика"
            Course.MONEY_ENERGY.name -> "Энергия денег"
            "Путь на миллион" -> "Путь на миллион"
            "Медитации" -> "Медитации"
            "Вес" -> "Психология лишнего веса"
            else -> null
        }
        findViewById<TextView>(R.id.nameCourse).text = courseTitle
        course = courseTitle

        findViewById<View>(R.id.test).setOnClickListener {
            if(courseTitle.equals("Медитации")) {
                val db = Firebase.firestore
                val uid = Firebase.auth.uid
                for(id in Repository.payList){
                    val medi = hashMapOf(
                        "user" to uid,
                        "medi" to id,
                    )
                    if(id == Repository.payList.get(Repository.payList.size-1)){
                        db.collection("pay_medi").document().set(medi).addOnCompleteListener {
                            finish()
                        }
                    }else {
                        db.collection("pay_medi").document().set(medi)
                    }

                }
                Repository.payList.clear()

            }
        }

        val sharedPreferences = getSharedPreferences("road", Context.MODE_PRIVATE)
        val savedAuthor = sharedPreferences.getString("current_author", "")

        if(intentAuthorName == null) { // если переход не с профиля коуча
            if(savedAuthor.equals("lena")) {
                author = Author.ELENA
            } else if (savedAuthor.equals("vas")) {
                author = Author.VASILIY
            } else author = Author.ELENA
        } else { // если переход с профиля
            if(intentAuthorName == Author.ELENA) {
                author = Author.ELENA
            } else if(intentAuthorName == "Екатерина") {
                author = Author.MILLION
            }else author = Author.VASILIY
        }
        val subscribe = findViewById<View>(R.id.subscribe)
        val subPrice = findViewById<View>(R.id.subPrice)
        subscribe.visibility = View.GONE
        if(courseTitle.equals("Психология лишнего веса"))
        {
            authorName = "Екатерины Кудрявцевой"
            priceInCents = 999_00
            //priceInCents = Repository.payList.size.toLong() * 100L * 100L
            tvPriceWithoutDiscount.text = Html.fromHtml("")
        }
        else if(courseTitle.equals("Медитации"))
        {
            authorName = "Медитации"
            priceInCents = 0
            priceInCents = Repository.payList.size.toLong() * 100L * 100L
            tvPriceWithoutDiscount.text = Html.fromHtml("")
        }
        else if(author == Author.ELENA) {
            priceInCents = 999_00
            authorName = "Екатерины Кудрявцевой"
            tvPriceWithoutDiscount.text = Html.fromHtml("Вместо <strike>5900 руб</strike>")
        } else if(author == Author.MILLION){
            subscribe.visibility = View.VISIBLE
            subPrice.setOnClickListener {
                val intent = Intent(this, AgreemenActivity::class.java)
                intent.putExtra("url", "file:///android_res/raw/accept_sub.html")
                startActivity(intent)

            }
            priceInCents = 999_00
            authorName = "Екатерины Кудрявцевой"
            tvPriceWithoutDiscount.text = Html.fromHtml("Вместо <strike>20000 руб</strike>")
        } else {
            authorName = "Василия Трубникова"
            priceInCents = 999_00
            tvPriceWithoutDiscount.text = Html.fromHtml("Вместо <strike>3900 руб</strike>")
        }

        val tvMonthPrice = findViewById<TextView>(R.id.tvMonthPrice)
        tvMonthPrice.text = "Полный курс ${priceInCents/100} Р"
        if(courseTitle.equals("Медитации")){
            tvMonthPrice.text = "Выбранные медитации ${priceInCents/100} Р"
        }
        val onlyYear = intent.getBooleanExtra("only_year", false)
        if(onlyYear){
            year.visibility = View.INVISIBLE
        }
        subscribe.setOnClickListener {
            val accept = findViewById<CheckBox>(R.id.accept)
            if(accept.isChecked) {
                priceInCents = 4000_00
                authorName = "Екатерины Кудрявцевой"
                payMonth()
            } else {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Предупрежнение")
                alert.setMessage("Вы должны согласиться с условиями подписки")
                alert.setPositiveButton("OK", null)
                alert.show()
            }

         }
        month.setOnClickListener {
            // Toast.makeText(this,"Полный курс", Toast.LENGTH_SHORT).show();
            payMonth()
        }
        year.setOnClickListener {
            // Toast.makeText(this,"Подписка на год", Toast.LENGTH_SHORT).show();
            payYear()
        }
        if(Repository.trial){
            trial.visibility = View.GONE
        }
       if(Repository.trialLena){
            trial.visibility = View.GONE
        }
        trial.setOnClickListener {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val uid = auth.currentUser!!.uid
            val user = hashMapOf(
                "trial" to true
            )
            db.collection("users")
                .document(uid)
                .update(user as Map<String, Any>)
                .addOnCompleteListener {
                    startActivity(Intent(this, BaseActivity::class.java))
                    finish()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.setScreenOpenAnalytics("Экран покупки", this::class.java.simpleName)
    }
}