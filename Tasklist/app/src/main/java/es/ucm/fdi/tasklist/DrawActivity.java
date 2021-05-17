package es.ucm.fdi.tasklist;

import android.graphics.Color;
import android.os.Bundle;
import java.util.UUID;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class DrawActivity extends AppCompatActivity implements OnClickListener {
    private ImageButton currPaint, drawBtn,baru,erase,save;
    private DrawingView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(Color.rgb(232, 232, 232));
        getWindow().setStatusBarColor(Color.rgb(232, 232, 232));
        setContentView(R.layout.activity_draw);
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        baru = (ImageButton)findViewById(R.id.new_btn);
        erase = (ImageButton)findViewById(R.id.erase_btn);
        save = (ImageButton)findViewById(R.id.save_btn);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn.setOnClickListener(this);
        baru.setOnClickListener(this);
        erase.setOnClickListener(this);
        save.setOnClickListener(this);
    }
    public void paintClicked(View view){
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.draw_btn){
            drawView.setupDrawing();
        }
        if(v.getId()==R.id.erase_btn){
            drawView.setErase(true);
            drawView.setBrushSize(drawView.getLastBrushSize());
        }
        if(v.getId()==R.id.new_btn){
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("Nuevo dibujo");
            newDialog.setMessage("¿Iniciar un nuevo dibujo (perderá el dibujo actual)?");
            newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            newDialog.show();
        }
        if(v.getId()==R.id.save_btn){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Guardar dibujo");
            saveDialog.setMessage("¿Guardar el dibujo en la Galería del dispositivo?");
            saveDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "dibujo");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "¡Dibujo guardado en la Galería!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "No se ha podido guardar la imagen.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();

                }
            });
            saveDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        this.activity = this;

        back = findViewById(R.id.image_draw_undo);
        draw_view = (com.divyanshu.draw.widget.DrawView) findViewById(R.id.draw_view);
        image_close_drawing = findViewById(R.id.image_close_drawing);
        image_close_drawing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func();
            }
        });
    }

   public void func(){
       Bitmap bitmap;
       View v1 = draw_view.getRootView();
       v1.setDrawingCacheEnabled(true);
       bitmap = Bitmap.createBitmap(v1.getDrawingCache());
       v1.setDrawingCacheEnabled(false);
   }
    @Override
    public void onClick(View v){
        if(v.getId()==R.id.image_close_drawing){

            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    draw_view.buildDrawingCache(true);
                    draw_view.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), draw_view.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if (imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Saved", Toast.LENGTH_LONG);
                        savedToast.show();
                    }else{
                        Toast unSaved = Toast.makeText(getApplicationContext(),
                                "Oops, Error, Image not saved", Toast.LENGTH_LONG);
                        unSaved.show();
                    }
                    draw_view.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            saveDialog.show();



            Bitmap bitmap = draw_view.getBitmap();
            String file_name = UUID.randomUUID() + ".png";

            File folder = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "TaskView");

            if (!folder.exists()){
                folder.mkdir();
            }
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(folder+File.separator+file_name);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                Toast.makeText(this, "picture saved", Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

    }



    }

}
*/

