package com.example.ledgerapp

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private val customersList = mutableListOf<String>()
    private val transactionsMap = mutableMapOf<String, MutableList<Pair<String, BigDecimal>>>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // إنشاء الواجهة برمجياً
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }

        val titleText = TextView(this).apply {
            text = "📖 دفتر الحسابات المالية"
            textSize = 22f
            setTextColor(Color.parseColor("#1E88E5"))
            setPadding(0, 0, 0, 30)
        }

        val addCustomerBtn = Button(this).apply {
            text = "+ إضافة عميل جديد"
            setBackgroundColor(Color.parseColor("#4CAF50"))
            setTextColor(Color.WHITE)
        }

        val listView = ListView(this).apply {
            setPadding(0, 20, 0, 0)
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, customersList)
        listView.adapter = adapter

        addCustomerBtn.setOnClickListener {
            showAddCustomerDialog()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val customerName = customersList[position]
            showCustomerDetailsDialog(customerName)
        }

        mainLayout.addView(titleText)
        mainLayout.addView(addCustomerBtn)
        mainLayout.addView(listView)

        setContentView(mainLayout)
    }

    private fun showAddCustomerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("إضافة عميل جديد")

        val input = EditText(this).apply {
            hint = "اسم العميل"
        }
        builder.setView(input)

        builder.setPositiveButton("حفظ") { _, _ ->
            val name = input.text.toString().trim()
            if (name.isNotEmpty()) {
                customersList.add(name)
                transactionsMap[name] = mutableListOf()
                adapter.notifyDataSetChanged()
                Toast.makeText(this, "تمت إضافة العميل $name", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("إلغاء", null)
        builder.show()
    }

    private fun showCustomerDetailsDialog(customerName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("حساب العميل: $customerName")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(30, 20, 30, 20)
        }

        val amountInput = EditText(this).apply {
            hint = "المبلغ"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val balanceText = TextView(this).apply {
            textSize = 18f
            setPadding(0, 20, 0, 20)
            updateBalanceText(this, customerName)
        }

        val btnGave = Button(this).apply {
            text = "أعطيته (له) +"
            setBackgroundColor(Color.parseColor("#E53935"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                addTransaction(customerName, amountInput.text.toString(), "GAVE", balanceText, amountInput)
            }
        }

        val btnGot = Button(this).apply {
            text = "قبضت منه (عليك) -"
            setBackgroundColor(Color.parseColor("#43A047"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                addTransaction(customerName, amountInput.text.toString(), "GOT", balanceText, amountInput)
            }
        }

        layout.addView(balanceText)
        layout.addView(amountInput)
        layout.addView(btnGave)
        layout.addView(btnGot)

        builder.setView(layout)
        builder.setPositiveButton("إغلاق", null)
        builder.show()
    }

    private fun addTransaction(
        customerName: String,
        amountStr: String,
        type: String,
        balanceTextView: TextView,
        input: EditText
    ) {
        val amount = amountStr.toBigDecimalOrNull()
        if (amount != null && amount > BigDecimal.ZERO) {
            transactionsMap[customerName]?.add(Pair(type, amount))
            updateBalanceText(balanceTextView, customerName)
            input.text.clear()
            Toast.makeText(this, "تم تسجيل العملية بنجاح", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "يرجى أدخال مبلغ صحيح", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateBalanceText(textView: TextView, customerName: String) {
        val txs = transactionsMap[customerName] ?: mutableListOf()
        var balance = BigDecimal.ZERO
        for (tx in txs) {
            if (tx.first == "GAVE") balance = balance.add(tx.second)
            else if (tx.first == "GOT") balance = balance.subtract(tx.second)
        }

        if (balance > BigDecimal.ZERO) {
            textView.text = "الرصيد الحالي: يطالب بـ $balance"
            textView.setTextColor(Color.RED)
        } else if (balance < BigDecimal.ZERO) {
            textView.text = "الرصيد الحالي: مطلوب منه ${balance.abs()}"
            textView.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            textView.text = "الرصيد الحالي: 0 (خالص)"
            textView.setTextColor(Color.BLACK)
        }
    }
}
