package info.iut.acy.fr.miniproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView MainMenu = (ListView)findViewById(R.id.MainMenuListView);

        //remplis la arraylist avec la m�thode fillHashMap
        ArrayList<HashMap<String, String>> appItemList = new ArrayList<HashMap<String, String>>();
        appItemList.add(fillHashMap("Mes entreprises", String.valueOf((R.drawable.entreprise))));

        // Cr�ation d'un SimpleAdapter qui met en correspondance les items pr�sents dans la list avec ceux de la vue
        SimpleAdapter itemsAdapter = new SimpleAdapter(this.getBaseContext(), appItemList, R.layout.main_menu_item,
                new String[]{"MenuTitle", "MenuIcon"}, new int[]{R.id.titre_menu, R.id.imageMenu});

        MainMenu.setAdapter(itemsAdapter);
    }

    // m�thode priv�e permettant de remplir un HashMap
    private HashMap<String, String> fillHashMap (String Title, String icon){
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("MenuTitle", Title);
        item.put("MenuIcon", icon);
        return item;
    }
}
