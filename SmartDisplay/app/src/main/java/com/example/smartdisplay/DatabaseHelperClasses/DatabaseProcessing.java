package com.example.smartdisplay.DatabaseHelperClasses;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import com.example.smartdisplay.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import android.widget.Toast;

public class DatabaseProcessing extends Fragment {
    //classı çağıran view elemanları yönetmek için alındı
    View root;

    //genel firabase bağlantı parametreleri
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser user;
    private FirebaseAuth auth;

    private MutableLiveData<Boolean> isCheckedCounter;//firabaseden counter alındıktan sonra işlem yapılması sağlandı-Bunun için bekleme sağlandı. Değişimden sonra gerekli classlardaki metotlar tetikledi.
    private int counter;
    private Boolean blockDouble;



    public DatabaseProcessing(View root) {
        this.root=root;
        counter=0;
        //kullancıya özel database bilgi ekleme/alma için eklendi
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        readUserCounterInfo();
    }


    public void readUserCounterInfo(){//counter yapısını datebasede oluşturarak taskların sıralamasını beliledik.

             blockDouble = true;//reference'de olan değişkeni değiştirdiğimiziçin onDataChange iki kere düşmesini engelledik.


                 reference = database.getReference(user.getUid() + "/counter");

                 reference.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         if (dataSnapshot.getValue() != null && blockDouble) {
                             blockDouble = false;
                             counter = Integer.parseInt(dataSnapshot.getValue().toString());
                             counter++;
                             reference.setValue(counter);
                             getCheckedCounter().postValue(true);
                         } else if (blockDouble) {
                             blockDouble = false;
                             reference.setValue(counter);
                             getCheckedCounter().postValue(true);
                         }

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError databaseError) {
                         Toast.makeText(root.getContext(), R.string.controlInternet, Toast.LENGTH_LONG).show();
                     }
                 });
    }


    public void saveTask(UserTask usrtask,String fromWhere){//counterdan yönlendirilen bilgiler database'e işlendi.

        if(fromWhere.equals("calendar")) {//calendardan gelenler C kodu ile kaydedildi, kolayca silinebilsin diye
            reference = database.getReference(user.getUid() + "/Tasks/" + counter+"C");//nereye kaydedileceğinin bilgisi.
            usrtask.setId(counter+"C");
        }
        else {
            reference = database.getReference(user.getUid() + "/Tasks/" + counter);//nereye kaydedileceğinin bilgisi.
            usrtask.setId(""+counter);
        }
        reference.setValue(usrtask);
        counter++;


    }

    public void updateCounterAfterSaveFinished(){//saveTask işleminden sonra burası çağrılarak en güncel counter firebasedede güncellenir.
        reference = database.getReference(user.getUid() + "/counter");
        reference.setValue(counter);
    }

    public MutableLiveData<Boolean> getCheckedCounter(){//getCheckedCounter dinlenildiği yer ile bağlantı.(observe yapısı)
        if(isCheckedCounter == null){
            isCheckedCounter = new MutableLiveData<>();
        }
        return isCheckedCounter;
    }


}
