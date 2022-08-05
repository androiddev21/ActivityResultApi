package com.example.activityresultapi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.example.activityresultapi.databinding.ActivityEditTextBinding

class EditTextActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTextBinding

    private val resultIntent: Intent
        get() = Intent().apply {
            putExtra(EXTRA_OUTPUT_MESSAGE, binding.textInputEditText.text.toString())
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTextBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bSave.setOnClickListener {
            onSavePressed()
        }
        binding.bCancel.setOnClickListener {
            onCancelPressed()
        }
        binding.textInputEditText.setText(intent.getStringExtra(EXTRA_INPUT_MESSAGE))
    }

    private fun onSavePressed() {
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun onCancelPressed() {
        setResult(RESULT_CANCELED, resultIntent)
        finish()
    }

    data class Output(val message: String)

    //создание собственного контракта
    //параметры дженерика - входящие и исходящее значения
    class Contract : ActivityResultContract<String, Output?>() {

        //преобразовывает входящее значение в intent для запуска activity
        //(той активити, с которой мы получим результат
        override fun createIntent(context: Context, input: String?): Intent =
            Intent(context, EditTextActivity::class.java).apply {
                putExtra(EXTRA_INPUT_MESSAGE, input)
            }

        //берет результат, тот который мы раньше обрабатывали в onActivityResult
        //нужно преобразовать в выходящий результат
        override fun parseResult(resultCode: Int, intent: Intent?): Output? {
            if (intent == null) return null
            val message = if (resultCode == RESULT_OK) intent.getStringExtra(EXTRA_OUTPUT_MESSAGE)
                ?: return null else return null
            return Output(message = message)
        }

        //можно проверить входящее значение на соответсвие какому-то либу значению
        //если значение соответсвует, то активити даже не будет запускаться, сразу выполнится
        //логика и вернется результат
        override fun getSynchronousResult(
            context: Context,
            input: String?
        ): SynchronousResult<Output?>? {
            //some logic
            return SynchronousResult(Output("some value"))
        }
    }

    companion object {
        private const val EXTRA_INPUT_MESSAGE = "extra_input"
        private const val EXTRA_OUTPUT_MESSAGE = "extra_output"
    }
}