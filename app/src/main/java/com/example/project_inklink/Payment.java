package com.example.project_inklink;

import com.stripe.android.ApiResultCallback;
import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import android.content.Context;

import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

public class Payment extends AppCompatActivity {

    private static final String PUBLISHABLE_KEY = "pk_test_51PFcETRuGDEFXxmkoe2mG3x29i7coZfhDNzGsr068efLeh9UIkfEShn70HOkF2AQyYp2ggkJYGEa66gLFq2TurtO00Qm4EmPrw"; // Replace with your publishable key

    private Stripe stripe;
    ImageView ivBack;
    TextView tvFragName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Stripe
        PaymentConfiguration.init(this, PUBLISHABLE_KEY);
        stripe = new Stripe(getApplicationContext(), PUBLISHABLE_KEY); // Pass the publishable key to the Stripe constructor
//        tvFragName=findViewById(R.id.toolbartitle);
//        tvFragName.setText("Payment");
//        ivBack=findViewById(R.id.ivtoolbarback);
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });
    }

    private void makePayment() {
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
        if (cardInputWidget != null) {
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                stripe.createPaymentMethod(
                        params,
                        new ApiResultCallback<PaymentMethod>() {
                            @Override
                            public void onSuccess(@NonNull PaymentMethod paymentMethod) {
                                Toast.makeText(Payment.this, "Payment successful", Toast.LENGTH_SHORT).show();
                                // You can handle successful payment here, e.g., send payment details to your server
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                Toast.makeText(Payment.this, "Error processing payment. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Invalid card details", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Card input widget not found", Toast.LENGTH_SHORT).show();
        }
    }
}