package com.example.greekrestaurant

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var orderDetails: String = "No orders yet"
    private val ordersList: MutableList<String> = mutableListOf()
    private var isDescriptionVisible = true
    private var isMenuOpen: Boolean = false
    private var isBookTableViewOpen: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bookTableButton: Button = findViewById(R.id.book_table)
        makeButtonBlink(bookTableButton)
        val descriptionTextView: TextView = findViewById(R.id.description)
        animateDescriptionTextSlide(descriptionTextView)
        initializeMainLayout()
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setContentView(R.layout.activity_main)
        initializeMainLayout()
        if (isMenuOpen) {
            openMenu()
        } else if (isBookTableViewOpen) {
            showBookTableView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isMenuOpen", isMenuOpen)
        outState.putBoolean("isBookTableViewOpen", isBookTableViewOpen)
        outState.putBoolean("isDescriptionVisible", isDescriptionVisible)
    }

    private fun restoreState(savedInstanceState: Bundle) {
        isMenuOpen = savedInstanceState.getBoolean("isMenuOpen", false)
        isBookTableViewOpen = savedInstanceState.getBoolean("isBookTableViewOpen", false)
        isDescriptionVisible = savedInstanceState.getBoolean("isDescriptionVisible", true)

        if (isMenuOpen) {
            openMenu()
        } else if (isBookTableViewOpen) {
            showBookTableView()
        }
    }

    private fun initializeMainLayout() {
        val menuButton: Button = findViewById(R.id.menu)
        val closeMenuButton: Button = findViewById(R.id.close_menu)
        val bookTableButton: Button = findViewById(R.id.book_table)
        val myOrderButton: Button = findViewById(R.id.orders)


        makeButtonBlink(bookTableButton)

        menuButton.setOnClickListener {
            openMenu()
        }

        closeMenuButton.setOnClickListener {
            closeMenu()
        }

        bookTableButton.setOnClickListener {
            isMenuOpen = false
            isBookTableViewOpen = true
            showBookTableView()
        }

        myOrderButton.setOnClickListener {
            showAllOrdersDialog()
        }
    }

    private fun openMenu() {
        val menuLayout: LinearLayout = findViewById(R.id.menu_layout)
        val descriptionTextView: TextView = findViewById(R.id.description)

        menuLayout.visibility = View.VISIBLE
        descriptionTextView.visibility = View.GONE
        isMenuOpen = true
        isDescriptionVisible = false
    }

    private fun closeMenu() {
        val menuLayout: LinearLayout = findViewById(R.id.menu_layout)
        val descriptionTextView: TextView = findViewById(R.id.description)

        menuLayout.visibility = View.GONE
        if (!isDescriptionVisible) {
            descriptionTextView.visibility = View.VISIBLE
        }
        isMenuOpen = false
    }

    private fun showBookTableView() {
        val bookTableLayout = layoutInflater.inflate(R.layout.dialog_book_table, null)
        setContentView(bookTableLayout)

        val closeButton: ImageButton = bookTableLayout.findViewById(R.id.close_button)
        val dateInput: EditText = bookTableLayout.findViewById(R.id.date_input)
        val timeInput: EditText = bookTableLayout.findViewById(R.id.time_input)
        val calendar = Calendar.getInstance()

        closeButton.setOnClickListener {
            setContentView(R.layout.activity_main)
            initializeMainLayout()
            isBookTableViewOpen = false
        }

        dateInput.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    dateInput.setText(dateFormat.format(selectedDate.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        timeInput.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val selectedTime =
                        String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    timeInput.setText(selectedTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val confirmBookingButton: Button = bookTableLayout.findViewById(R.id.confirm_booking)
        confirmBookingButton.setOnClickListener {
            val nameInput: EditText = bookTableLayout.findViewById(R.id.name_input)
            val smokingOption: CheckBox = bookTableLayout.findViewById(R.id.smoking_zone)
            val seatsSpinner: Spinner = bookTableLayout.findViewById(R.id.seats_spinner)
            val veganOption: CheckBox = bookTableLayout.findViewById(R.id.vegan_option)
            val paymentGroup: RadioGroup = bookTableLayout.findViewById(R.id.payment_method_group)

            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val time = timeInput.text.toString().trim()
            if (time.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_time), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val date = dateInput.text.toString().trim()
            if (date.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_date), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (paymentGroup.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this,
                    getString(R.string.please_choose_payment_method),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val isVegan =
                if (veganOption.isChecked) getString(R.string.vegan_yes) else getString(R.string.vegan_no)
            val isSmoking =
                if (smokingOption.isChecked) getString(R.string.smoke_yes) else getString(R.string.smoke_no)

            orderDetails = """
                ${getString(R.string.name)}: $name
                ${getString(R.string.seats)}: ${seatsSpinner.selectedItem}
                ${getString(R.string.date)}: $date
                ${getString(R.string.time)}: $time
                ${getString(R.string.vegan)}: $isVegan
                ${getString(R.string.smoking_zone)}: $isSmoking
                ${getString(R.string.payment)}: ${findViewById<RadioButton>(paymentGroup.checkedRadioButtonId).text}
            """.trimIndent()

            animateButtonScaleAndRotate(confirmBookingButton) {
                ordersList.add(orderDetails)
                Toast.makeText(this, getString(R.string.order_saved), Toast.LENGTH_SHORT).show()
                setContentView(R.layout.activity_main)
                initializeMainLayout()
                isBookTableViewOpen = false
            }
        }
    }

    private fun showAllOrdersDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_details, null)
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val ordersListView: ListView = dialogView.findViewById(R.id.orders_list_view)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ordersList)
        ordersListView.adapter = adapter

        ordersListView.setOnItemLongClickListener { _, _, position, _ ->
            ordersList.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, getString(R.string.order_deleted), Toast.LENGTH_SHORT).show()
            true
        }

        val closeButton: Button = dialogView.findViewById(R.id.close_all_orders_dialog)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun animateButtonScaleAndRotate(button: Button, onFinish: () -> Unit) {
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1f, 1.5f, 1f).setDuration(500)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 1.5f, 1f).setDuration(500)
        val rotate = ObjectAnimator.ofFloat(button, "rotation", 0f, 360f).setDuration(500)

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(scaleX, scaleY, rotate)

        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                onFinish()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        animatorSet.start()
    }

    private fun animateDescriptionTextSlide(textView: TextView){
        textView.translationX = -1000f
        textView.animate().translationX(0f).setDuration(1000).start()
    }

    private fun makeButtonBlink(button: Button) {
        val blinkAnimation = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.5f, 1f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        blinkAnimation.start()

    }
}
