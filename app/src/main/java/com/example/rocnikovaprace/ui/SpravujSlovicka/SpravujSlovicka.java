package com.example.rocnikovaprace.ui.SpravujSlovicka;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.databinding.FragmentGalleryBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

public class SpravujSlovicka extends Fragment {

    private SpravujSlovickaModel spravujSlovickaModel;
    private @NonNull FragmentGalleryBinding binding;
    String nazevslova = "";
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    ImageButton imageButton;
    RecyclerView recyclerView;
    ArrayList<Slovicka> source;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    Adapter adapter;
    LinearLayoutManager HorizontalLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        spravujSlovickaModel =
                new ViewModelProvider(this).get(SpravujSlovickaModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Vytvo???? dialog pro zad??n?? hesla, bez kter??ho se ned?? vstoupit do tohoto fragmentu
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.zadejte_heslo));

        // Nastav?? vzhled dialogu
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.heslodialog,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {

                    //Ov?????? spr??vnost hesla
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        EditText editText
                                = customLayout
                                .findViewById(
                                        R.id.dialogoveheslo);
                        String zeSouboru = "";
                        File heslosoubor = new File(getContext().getFilesDir(), "heslo.txt");
                        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
                            zeSouboru = br.readLine();
                        } catch (Exception e) {
                            System.out.println("Chyba p??i ??ten?? ze souboru.");
                        }
//Pokud je heslo ??patn?? vr??t?? u??ivatele na domovskou obrazovku
                        if (zeSouboru.equals(editText.getText().toString())) {
                        } else {
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }


                    }
                });

        // Zobraz?? dialog
        AlertDialog dialog
                = builder.create();
        dialog.show();


        recyclerView
                = (RecyclerView) root.findViewById(
                R.id.recyclerview);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());

        // P??i??ad?? LayoutManager k Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        // P??id?? polo??ky do seznamu
        AddItemsToRecyclerViewArrayList();

        // Zavol?? konstruktor
        RecyclerViewClickInterface inter = new RecyclerViewClickInterface() {
            @Override
            public void setClick(int abc) {


                AlertDialog.Builder builder
                        = new AlertDialog.Builder(getContext());

                File heslosoubor = new File(getContext().getFilesDir(), "zkouska.txt");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(heslosoubor, false))) {
                    bw.write("ne");
                    bw.newLine();
                    bw.flush();

                } catch (Exception e) {
                    System.out.println("Do souboru se nepovedlo zapsat.");
                }

                // Nastav?? vzhled dialogu
                final View customLayout
                        = getLayoutInflater()
                        .inflate(
                                R.layout.layoutzmen,
                                null);

                EditText editText
                        = customLayout
                        .findViewById(
                                R.id.Slovonove);
                editText.setHint(source.get(abc).slovo.toString());


                builder.setView(customLayout);
                builder.setCancelable(true);
                builder.setPositiveButton(
                        getString(R.string.ulozit_zmeny),
                        new DialogInterface.OnClickListener() {

                            //Ov?????? spr??vnost hesla
                            @RequiresApi(api = Build.VERSION_CODES.S)
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                //Prohl??dne v??echna slov????ka a pokud, u?? takov?? slov????ko existuje, upozorn?? na to u??ivatele
                                int p = 0;
                                while (p < source.size()) {
                                    if (editText.getText().toString().equals(source.get(p).slovo.toString()) && p != abc) {
                                        AlertDialog dialog2 = new AlertDialog.Builder(getContext())
                                                .setMessage(getString(R.string.vyzva1))
                                                .setPositiveButton("ok", null)
                                                .show();
                                        return;
                                    }
                                    p++;
                                }
                                // Pokud u??ivatel nezadal n??zev slov????ka, vytvo???? dialog, kter?? ho na to upozorn??
                                if (editText.getText() == null || editText.getText().toString().equals("")) {
                                    AlertDialog dialog2 = new AlertDialog.Builder(getContext())
                                            .setMessage(getString(R.string.vyzva2))
                                            .setPositiveButton("ok", null)
                                            .show();
                                    return;

                                }

                                BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                                Bitmap bitmapa = drawable.getBitmap();
                                Slovicka sl = new Slovicka(editText.getText().toString(), bitmapa);
                                source.set(abc, sl);
                                adapter.notifyItemChanged(abc);
                                File file = new File(getContext().getFilesDir(), "slovicka.txt");
                                BitmapDrawable drawable2 = (BitmapDrawable) imageButton.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();
                                new ImageSaver(getContext()).
                                        setFileName(source.get(abc).slovo + ".png").
                                        setDirectoryName(file.getName()).
                                        save(bitmap);


                            }
                        });


                // Zobraz?? dialog
                AlertDialog dialog
                        = builder.create();
                dialog.show();


            }
        };
        adapter = new Adapter(source, getContext(), inter);

        // Nastav?? Horizontal Layout Manager pro Recycler view
        HorizontalLayout
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);

        // Nastav?? adapter pro recycler view
        recyclerView.setAdapter(adapter);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        return root;
    }


    // Ulo???? zm??n??n?? data
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        int i = 0;
        File file = new File(getContext().getFilesDir(), "slovicka.txt");

        try {
            FileWriter fw = new FileWriter(file, false);
            PrintWriter pw = new PrintWriter(fw, false);
            pw.flush();
            pw.close();
            fw.close();
        } catch (Exception exception) {

            System.out.println("Chyba v maz??n??");

        }

        while (i < source.size()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(source.get(i).toString());
                bw.newLine();
                bw.flush();


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }

            i++;

        }


        binding = null;
    }

    //P??id?? polo??ky do seznamu
    public void AddItemsToRecyclerViewArrayList() {
        source = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "slovicka.txt");
        //Nejd????v je na??te ze souboru
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            int p = 0;
            while ((s = br.readLine()) != null) {
                Bitmap bitmap = new ImageSaver(getContext()).setFileName(s + ".png").setDirectoryName(file.getName()).load();
                Slovicka slovo = new Slovicka(s, bitmap);
                //Potom je p??id?? do ArrayListu
                source.add(slovo);
                p++;
            }
        } catch (Exception e) {
            System.out.println("Chyba p??i ??ten?? ze souboru.");
        }


    }


    //Umo????uje p??esouvat polo??ky v RecycleView
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(source, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        //Po p??et??hnut?? nahoru, nebo dolu sma??e polo??ku ze seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            nazevslova = source.get(position).toString();
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            //Vytvo???? SnacBar s tla????tkem zp??t, kter?? umo????uje vr??tit smazanou polo??ku zp??t
            Snackbar.make(recyclerView, nazevslova, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.zpet), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File file = new File(getContext().getFilesDir(), "slovicka.txt");
                            Bitmap bitmap2 = new ImageSaver(getContext()).setFileName(nazevslova + ".png").setDirectoryName(file.getName()).load();
                            Slovicka slovo = new Slovicka(nazevslova, bitmap2);
                            source.add(position, slovo);
                            recyclerView.getAdapter().notifyItemInserted(position);


                        }
                    }).show();


        }
    };


}