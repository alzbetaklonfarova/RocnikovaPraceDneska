package com.example.rocnikovaprace.ui.Organizace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.ImageSaver;
import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.MalyAdapter;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.Adaptery.StredniAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;

public class Organizace extends Fragment implements MalyAdapter.onNoteListener {

    private OrganizaceViewModel mViewModel;
    RecyclerView recyclerView;
    RecyclerView recyclerView2;
    ArrayList<Slovicka> source;
    ArrayList<Slovicka> source2;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerView.LayoutManager RecyclerViewLayoutManager2;
    MalyAdapter adapter;
    StredniAdapter adapter2;
    LinearLayoutManager HorizontalLayout;
    LinearLayoutManager HorizontalLayout2;

    public static Organizace newInstance() {
        return new Organizace();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        File file = new File(getContext().getFilesDir(), "rozvrh.txt");
        if (source2.size() > 0) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                bw.write(source2.get(0).slovo);
                bw.newLine();
                bw.flush();
                source2.remove(0);


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }

        }
        while (0 < source2.size()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(source2.get(0).slovo);
                bw.newLine();
                bw.flush();
                source2.remove(0);


            } catch (Exception e) {
                System.out.println("Do souboru se nepovedlo zapsat.");
            }
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizace_fragment, container, false);

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
                = (RecyclerView) view.findViewById(
                R.id.recyclerview103);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());


        recyclerView2
                = (RecyclerView) view.findViewById(
                R.id.recyclerview102);
        RecyclerViewLayoutManager
                = new

                LinearLayoutManager(
                getContext());


        // P??i??ad?? LayoutManager k Recycler View
        recyclerView.setLayoutManager(
                RecyclerViewLayoutManager);

        recyclerView2.setLayoutManager(
                RecyclerViewLayoutManager2);

        // P??id?? polo??ka do ArrayListu
        AddItemsToRecyclerViewArrayList();
        AddItemsToRecyclerViewArrayList2();

        // Zavol?? konstruktor
        adapter = new MalyAdapter(source);
        adapter2 = new StredniAdapter(source2);

        // Nastav?? Horizontal Layout Manager pro Recycler view
        HorizontalLayout
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView.setLayoutManager(HorizontalLayout);


        HorizontalLayout2
                = new

                LinearLayoutManager(
                getActivity().

                        getApplicationContext(),

                LinearLayoutManager.HORIZONTAL,
                false);
        recyclerView2.setLayoutManager(HorizontalLayout2);


        // Set adapter on recycler view
        recyclerView.setAdapter(adapter);
        recyclerView2.setAdapter(adapter2);


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(simpleCallback2);
        itemTouchHelper2.attachToRecyclerView(recyclerView2);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrganizaceViewModel.class);
        // TODO: Use the ViewModel
    }

    //P??id?? polo??ky do seznamu
    public void AddItemsToRecyclerViewArrayList() {
        source = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "aktivity.txt");
        //Na??te slov????ka ze souboru
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            int p = 0;
            while ((s = br.readLine()) != null) {
                Bitmap bitmap = new ImageSaver(getContext()).setFileName(s + ".png").setDirectoryName(file.getName()).load();
                Slovicka slovo = new Slovicka(s, bitmap);
                //P??id?? je do ArrayListu
                source.add(slovo);
                p++;
            }
        } catch (Exception e) {
            System.out.println("Chyba p??i ??ten?? ze souboru.");
        }


    }


    public void AddItemsToRecyclerViewArrayList2() {
        source2 = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "rozvrh.txt");
        File file2 = new File(getContext().getFilesDir(), "aktivity.txt");
        //Na??te slov????ka ze souboru
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            while ((s = br.readLine()) != null) {
                Bitmap bitmap = new ImageSaver(getContext()).setFileName(s + ".png").setDirectoryName(file2.getName()).load();
                Slovicka slovo = new Slovicka(s, bitmap);
                //P??id?? je do ArrayListu
                source2.add(slovo);
            }
        } catch (Exception e) {
            System.out.println("Chyba p??i ??ten?? ze souboru.");
        }

    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.UP) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            return false;
        }

        //Po p??et??hnut?? p??id???? tuto polo??ku do vrchn??ho seznamu
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Slovicka sl = source.get(position);
            source2.add(source.get(position));
            source.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
            source.add(position, sl);
            recyclerView.getAdapter().notifyItemInserted(position);
            recyclerView2.getAdapter().notifyItemInserted(source2.size());

        }
    };


    ItemTouchHelper.SimpleCallback simpleCallback2 = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(source2, fromPosition, toPosition);
            recyclerView2.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }


        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Slovicka slovicka = source2.get(position);
            //source.add(slovicka);
            source2.remove(position);
            recyclerView2.getAdapter().notifyItemRemoved(position);
            //recyclerView.getAdapter().notifyItemInserted(source.size());


        }
    };


}