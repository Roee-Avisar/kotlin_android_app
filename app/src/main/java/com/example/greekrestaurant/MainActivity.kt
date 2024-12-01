package com.example.greekrestaurant

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
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
    private val ordersList : MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeMainLayout()


        val saladImage: ImageView = findViewById(R.id.salad_img)
        setUpImageHoldAnimation(saladImage)
        val kababImage: ImageView = findViewById(R.id.kabab_img)
        setUpImageHoldAnimation(kababImage)
        val salmonImage: ImageView = findViewById(R.id.salmon_img)
        setUpImageHoldAnimation(salmonImage)
        val fishImage: ImageView = findViewById(R.id.fish_img)
        setUpImageHoldAnimation(fishImage)
        val orengeImage: ImageView = findViewById(R.id.orenge_img)
        setUpImageHoldAnimation(orengeImage)
        val colaImage: ImageView = findViewById(R.id.cola_img)
        setUpImageHoldAnimation(colaImage)
        val smoodiImage: ImageView = findViewById(R.id.smoodi_img)
        setUpImageHoldAnimation(smoodiImage)

    }

    private fun initializeMainLayout() {
        val menuButton: Button = findViewById(R.id.menu)
        val menuLayout: LinearLayout = findViewById(R.id.menu_layout)
        val closeMenuButton: Button = findViewById(R.id.close_menu)
        val bookTableButton: Button = findViewById(R.id.book_table)
        val mainLayout: View = findViewById(R.id.main)
        val myOrderButton: Button = findViewById(R.id.orders)

        menuButton.setOnClickListener {
            menuLayout.visibility = View.VISIBLE
        }

        closeMenuButton.setOnClickListener {
            menuLayout.visibility = View.GONE
        }

        bookTableButton.setOnClickListener {
            val bookTableLayout = layoutInflater.inflate(R.layout.dialog_book_table, null)

            setContentView(bookTableLayout)
            val closeButton: ImageButton = bookTableLayout.findViewById(R.id.close_button)
            val dateInput: EditText = bookTableLayout.findViewById(R.id.date_input)
            val timeInput: EditText = bookTableLayout.findViewById(R.id.time_input)
            val calendar = Calendar.getInstance()

            closeButton.setOnClickListener{
                //animateImageButtonColor(closeButton, Color.WHITE, Color.RED)
                setContentView(R.layout.activity_main)
                initializeMainLayout()
            }

            // Listener לבחירת תאריך
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

            // Listener לבחירת שעה
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

            // כפתור אישור ההזמנה
            val confirmBookingButton: Button = bookTableLayout.findViewById(R.id.confirm_booking)
            confirmBookingButton.setOnClickListener {
                val nameInput : EditText = bookTableLayout.findViewById(R.id.name_input)
                val smookingOption : CheckBox = bookTableLayout.findViewById(R.id.smoking_zone)
                val seatsSpinner: Spinner = bookTableLayout.findViewById(R.id.seats_spinner)
                val veganOption: CheckBox = bookTableLayout.findViewById(R.id.vegan_option)
                val paymentGroup: RadioGroup = bookTableLayout.findViewById(R.id.payment_method_group)
                val selectedPaymentMethodId = paymentGroup.checkedRadioButtonId
                val selectedPaymentMethod =
                    bookTableLayout.findViewById<RadioButton>(selectedPaymentMethodId)?.text.toString()

                val name = nameInput.text.toString().trim()
                if (name.isEmpty()){
                    Toast.makeText(this, getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val time = timeInput.text.toString().trim()
                if(time.isEmpty()){
                    Toast.makeText(this, getString(R.string.please_select_time), Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                val date = dateInput.text.toString().trim()
                if(date.isEmpty()){
                    Toast.makeText(this, getString(R.string.please_select_date), Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

                if (paymentGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(this,
                        getString(R.string.please_choose_payment_method), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val isVegan = if (veganOption.isChecked) getString(R.string.vegan_yes) else getString(R.string.vegan_no)
                val isSmoking = if (smookingOption.isChecked) getString(R.string.smoke_yes) else getString(R.string.smoke_no)

                orderDetails = """
                    ${getString(R.string.name)}: $name
                    ${getString(R.string.seats)}: ${seatsSpinner.selectedItem}
                    ${getString(R.string.date)}: $date
                    ${getString(R.string.time)}: $time
                    ${getString(R.string.vegan)}: $isVegan
                    ${getString(R.string.smoking_zone)} : $isSmoking
                    ${getString(R.string.payment)}: ${findViewById<RadioButton>(paymentGroup.checkedRadioButtonId).text}

                """.trimIndent()

                ordersList.add(orderDetails)
                Toast.makeText(this, getString(R.string.order_saved), Toast.LENGTH_SHORT).show()
                setContentView(R.layout.activity_main)
                initializeMainLayout()
            }
        }

        myOrderButton.setOnClickListener {
            showAllOrdersDialog()
        }
    }

    private fun showAllOrdersDialog() {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_details, null)

        // Create the dialog
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Find the ListView in the dialog layout
        val ordersListView: ListView = dialogView.findViewById(R.id.orders_list_view)

        // Set up the adapter for the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ordersList)
        ordersListView.adapter = adapter

        // Set a long click listener for deleting orders
        ordersListView.setOnItemLongClickListener { _, _, position, _ ->
            ordersList.removeAt(position)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Order deleted", Toast.LENGTH_SHORT).show()
            true
        }

        // Close button to dismiss the dialog
        val closeButton: Button = dialogView.findViewById(R.id.close_all_orders_dialog)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
//animation
    private fun setupImageLongPressAnimation(imageView: ImageView) {
        imageView.setOnLongClickListener {
            val scaleUpX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 2f)
            val scaleUpY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 2f)
            val scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", 2f, 1f)
            val scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", 2f, 1f)

            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(scaleUpX, scaleUpY, scaleDownX, scaleDownY)
            animatorSet.duration = 500
            animatorSet.start()
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpImageHoldAnimation(imageView: ImageView){
        val originalWidth = imageView.layoutParams.width
        val originalHeight = imageView.layoutParams.height
        val handler = android.os.Handler()
        var isLongPress = false

        val longPressRunnable = Runnable {
            isLongPress = true
            val params = imageView.layoutParams
            params.width = originalWidth * 2
            params.height = originalHeight * 2
            imageView.layoutParams = params
        }

        imageView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPress = false
                    handler.postDelayed(longPressRunnable, 500)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(longPressRunnable)
                    if (isLongPress) {
                        val params = imageView.layoutParams
                        params.width = originalWidth
                        params.height = originalHeight
                        imageView.layoutParams = params
                    }
                }
            }
            true
        }
    }

    private fun animateImageButtonColor(imageButton: ImageButton,statColor: Int,endColor: Int){
        val colorAnimation = ValueAnimator.ofArgb(statColor,endColor)
        colorAnimation.duration = 1000
        colorAnimation.addUpdateListener { animator ->
            imageButton.setColorFilter(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }
}

