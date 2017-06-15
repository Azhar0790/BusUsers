package busdriver.com.vipassengers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by WIN 10 on 12/18/2016.
 */
public class Package_activity extends AppCompatActivity {
    RadioGroup rg_packages;
    Button btn_apply;
    RadioButton rb_pack;
    SharedPreferences pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_package);
        pref=getSharedPreferences("Pref",MODE_PRIVATE);
        btn_apply = (Button) findViewById(R.id.btn_apply);
        rg_packages = (RadioGroup) findViewById(R.id.rg_packages);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg_packages.getCheckedRadioButtonId();
                rb_pack = (RadioButton) findViewById(selectedId);
                Toast.makeText(getApplicationContext(), "You choosed " + rb_pack.getText(), Toast.LENGTH_SHORT).show();
                if (rb_pack.getText().equals("$49 per Month.")) {
                    Intent i = new Intent(getApplicationContext(), PaymentStripe.class);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("price", "$49");
                    editor.commit();
                    startActivity(i);
                }
            }
        });
    }
}
