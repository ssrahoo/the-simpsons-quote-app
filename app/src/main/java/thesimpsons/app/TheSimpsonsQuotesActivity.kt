package thesimpsons.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import thesimpsons.app.databinding.ActivityTheSimpsonsQuotesBinding
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.Runnable
import java.io.IOException
import java.io.InputStream
import java.net.URL

class TheSimpsonsQuotesActivity : ComponentActivity() {

    private lateinit var binding : ActivityTheSimpsonsQuotesBinding

    data class Quote(var quote:String, var character:String, var image:String, var characterDirection:String)

    private val JSON_FILE:String = "quotes.json"

    private lateinit var quotes:Array<Quote>

//    private lateinit var valueAnimator : ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTheSimpsonsQuotesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        init()
        update()
    }

    class QuoteBuilder(var binding : ActivityTheSimpsonsQuotesBinding, var quote: Quote) : Thread() {
        lateinit var bitmap : Bitmap
        var handler : Handler = Handler()
        override fun run() {
            var inputStream : InputStream? = null

            try {
                // url to bitmap
                inputStream = URL(quote.image).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            }catch (e: IOException){
                e.printStackTrace()
            }

            handler.post(Runnable {
                binding.ivCharacter.setImageBitmap(bitmap)
                binding.tvQuote.setText(quote.quote)
                binding.tvCharacter.setText(quote.character)
                hideProgressBar()
            })
        }
        private fun hideProgressBar() { binding.indeterminateBar.visibility = View.INVISIBLE }
    }


    private fun init() {
        clear()
        quotes = jacksonObjectMapper().readValue(assets.open(JSON_FILE))
        binding.btnRandom.setOnClickListener { update() }

//        //button animation // wip
//        valueAnimator = ValueAnimator.ofInt(0, 100)
//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE)
//        valueAnimator.setInterpolator(LinearInterpolator())
//        valueAnimator.setDuration(2000L)
//        valueAnimator.addUpdateListener {
//            ValueAnimator.AnimatorUpdateListener(){ animation ->
//                binding.btnRandom.setTranslationX(animation.getAnimatedValue().toString().toFloat())
//            }
//        }
//        valueAnimator.start()
    }

    private fun update() {
        clear()
        showProgressBar()
        QuoteBuilder(binding, quotes[randomIntWithinRange(0, quotes.size - 1)]).start()
    }

    private fun clear(){
        binding.tvQuote.setText("")
        binding.tvCharacter.setText("")
        binding.ivCharacter.setImageBitmap(null)
        binding.btnRandom.setContentDescription("")
    }

    private fun showProgressBar() { binding.indeterminateBar.visibility = View.VISIBLE }

    private fun randomIntWithinRange(inclusiveMin:Int, inclusiveMax:Int) : Int = inclusiveMin + (Math.random() * (inclusiveMax - inclusiveMin + 1)).toInt()

}