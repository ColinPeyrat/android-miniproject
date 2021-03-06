package info.iut.acy.fr.miniproject.Contact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import info.iut.acy.fr.miniproject.Database.ContactAdapter;
import info.iut.acy.fr.miniproject.Database.TraineeshipAdapter;
import info.iut.acy.fr.miniproject.R;

import java.util.HashMap;

public class ContactActivity extends Activity implements View.OnClickListener {

    // Définition des éléments du layout
    private HashMap<String,Long> companys;
    private Spinner companySpinner;
    private ListView lvContact;
    private ContactAdapter ContactDB;
    private TraineeshipAdapter CompanyDB;
    private String Sort = "ASC";
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact);
        // Binding des éléments de la vue
        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(this);

        companySpinner = (Spinner)findViewById(R.id.SpinnerCompany);
        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            /**
             * Métode OnItemSelect qui réagit en fonction de l'item qui a été selectionné
             *
             * @param View  La vue de l Adapter qui a été cliquée
             * @param position La position de la vue dans l'adapter
             * @param id: L'Id de l'item cliqué
             */
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("OnItemSelected",String.valueOf(id));
                Log.i("OnItemSelected",parent.getSelectedItem().toString());

                switch ((int) id){
                    case 0:
                        populate();
                        break;
                    default:
                        Long idcompany = companys.get(parent.getSelectedItem().toString());
                        populate(idcompany);
                        break;
                }
            }

            @Override
            /**
             * Métode utilisé quand aucun élément n'est sélectionné
             */
            public void onNothingSelected(AdapterView<?> parent) {
                populate();
            }
        });

        ContactDB = new ContactAdapter(getApplicationContext());
        CompanyDB = new TraineeshipAdapter(getApplicationContext());

        lvContact = (ListView) findViewById(R.id.ListViewContact);

        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapter,
                                           View v, int position, final long id) {
                final AlertDialog.Builder b = new AlertDialog.Builder(ContactActivity.this);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setMessage("Supprimer ce contact ?");
                b.setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                Toast.makeText(getApplicationContext(), "Supprimé", Toast.LENGTH_LONG).show();

                                // supprime la ligne dans la base de donnée correspondant a l'item dans la ListView
                                ContactDB.removeContact(id);

                                //rafraichis la liste view
                                populate();
                            }
                        });
                b.setNegativeButton("Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                b.show();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContactDB.open();
        CompanyDB.open();

        // Rafraichis la ListView
        populate();

        Cursor company = CompanyDB.getAllCompany();

        String[] spinnerArray =  new String[company.getCount()+1];
        spinnerArray[0] = "Toutes les entreprises";
        // Spinner Drop down elements
        companys = new HashMap<String, Long>();
        companys.put("Toutes les entreprises", (long) 0);

        if (company != null ) {
            if  (company.moveToFirst()) {
                Integer i=1;
                do {
                    String name = company.getString(company.getColumnIndex(TraineeshipAdapter.KEY_NAME));
                    Long id = Long.valueOf(company.getString(company.getColumnIndexOrThrow(TraineeshipAdapter.KEY_ID)));
                    companys.put(name,id);
                    spinnerArray[i] = name;
                    i++;
                }while (company.moveToNext());
            }
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapterCompanys = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);

        // Drop down layout style - list view with radio button
        dataAdapterCompanys.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        companySpinner.setAdapter(dataAdapterCompanys);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAdd:
                Intent intentAddContact = new Intent(getBaseContext(),AddContactActivity.class);
                startActivity(intentAddContact);
                // avertis l'utilisateur par un toast si c'est le cas
                // populate();
                break;
            case R.id.btnRefresh:
                Log.i("btnRefresh",Sort);

                if(Sort == "ASC"){
                    Sort = "DESC";
                    //met un drawableleft
                    btnRefresh.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sort_date, 0, 0, 0);

                }
                else if(Sort == "DESC"){
                    Sort = "ASC";
                    btnRefresh.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_name, 0, 0, 0);
                }

                if(companySpinner.getSelectedItemPosition() == 0)
                    populate();
                else
                    populate(companys.get(companySpinner.getSelectedItem().toString()));
                break;
        }
    }

    // alimentation de la liste par le contenu de la base de donn�es
    private void populate(){
        Cursor contactCursor = ContactDB.getAllContact(Sort);
        // Setup cursor adapter using cursor from last step
        ContactCursorAdapter todoAdapter = new ContactCursorAdapter(this, contactCursor);
        // Attach cursor adapter to the ListView
        lvContact.setAdapter(todoAdapter);
    }

    private void populate(Long company){
        Cursor contactCursor = ContactDB.getContactByCompany(company,Sort);
        // Setup cursor adapter using cursor from last step
        ContactCursorAdapter todoAdapter = new ContactCursorAdapter(this, contactCursor);
        // Attach cursor adapter to the ListView
        lvContact.setAdapter(todoAdapter);
    }

}
