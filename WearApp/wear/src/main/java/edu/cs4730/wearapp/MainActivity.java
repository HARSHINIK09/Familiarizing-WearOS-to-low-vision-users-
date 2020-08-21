package edu.cs4730.wearapp;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.wearable.activity.WearableActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;



public class MainActivity extends WearableActivity {
//time,x,y,R,C,sound output
    //private TextView mTextView;
    Random myRandom = new Random();
    ImageButton ib;
    private Context mContext;
    private Resources mResources;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private Button mButton;
    private ImageView mImageView;
    TextToSpeech t1;
    TextToSpeech tts;
    int i=0;
    Map map=new HashMap();
    StringBuffer color = new StringBuffer("nothing");
    int direction=-1;
    int repeatVar=0;
    int correctVar=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String myData = "";
        File myExternalFile = new File("/data/user/0/edu.cs4730.wearapp/files/","Applog");
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine + "\n";
            }
            br.close();
            in.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("**************************"+myData);
        PixelGridView pixelGrid = new PixelGridView(this);
        pixelGrid.setNumColumns(3);//onCreate(new Bundle());
        pixelGrid.setNumRows(3);
        setContentView(pixelGrid);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PixelGridView pixelGrid = new PixelGridView(this);
        try{
            pixelGrid.outputStream.close();
            tts.stop();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public class PixelGridView extends View {
        private int numColumns, numRows;
        private float cellWidth, cellHeight,cellWidth7,cellHeight7;
        private float rectHeight,rectWidth;
        private Paint blackPaint = new Paint();
        private TextToSpeech tts;
        FileOutputStream outputStream;
        //private Paint redPaint = new Paint();
        private boolean[][] cellChecked;

        public PixelGridView(Context context) {
            this(context, null);
        }

        public PixelGridView(Context context, AttributeSet attrs) {
            super(context, attrs);
            blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        public void setNumColumns(int numColumns) {
            this.numColumns = numColumns;
            calculateDimensions();
        }

        public int getNumColumns() {
            return numColumns;
        }

        public void setNumRows(int numRows) {
            this.numRows = numRows;
            calculateDimensions();
        }

        public int getNumRows() {
            return numRows;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            calculateDimensions();
        }

        private void calculateDimensions() {
            if (numColumns < 1 || numRows < 1) {
                return;
            }


            cellChecked = new boolean[numColumns][numRows];
//2*cellWidt7
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.BLACK);

            if (numColumns == 0 || numRows == 0) {
                return;
            }

            float width = getWidth();
            float height = getHeight();



            cellWidth7 = getWidth() / 7;
            System.out.println("CELL-----------"+cellWidth7);
            cellHeight7 = getHeight() /7 ;
            cellWidth = (getWidth()-(2*cellWidth7)) / numColumns;
            cellHeight = (getHeight()-(2*cellHeight7)) / numRows;
            canvas.drawLine(cellWidth7 +3f, cellWidth7,cellWidth7 +3f, height-(cellHeight7), blackPaint);//YL
            canvas.drawLine((cellWidth7), 6*(cellWidth7) +5f, width-(cellWidth7), 6*(cellWidth7) +5f, blackPaint);//XB
            canvas.drawLine(6*(cellWidth7), cellWidth7,6*cellWidth7, height-(cellHeight7), blackPaint);//YR
            canvas.drawLine((cellWidth7), cellHeight7, width-(cellWidth7), cellHeight7, blackPaint);//XT

            rectHeight=(height-(cellHeight7)-cellWidth7)/3;
            rectWidth=(width-(cellWidth7)-(cellWidth7))/3;

            canvas.drawLine(1.6f*rectWidth, cellWidth7,1.6f*rectWidth, height-(cellHeight7), blackPaint);
            canvas.drawLine(2.6f*rectWidth, cellWidth7,2.6f*rectWidth, height-(cellHeight7), blackPaint);
            canvas.drawLine((cellWidth7), 1.6f*rectHeight, width-(cellWidth7), 1.6f*rectHeight, blackPaint);
            canvas.drawLine((cellWidth7), 2.6f*rectHeight,  width-(cellWidth7), 2.6f*rectHeight, blackPaint);
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            canvas.drawRect(cellWidth7 +3f, cellHeight7,1.6f*rectHeight, 1.6f*rectWidth, paint);//00  X>=cellWidth7 +3f | X<1.6f*rectHeight| Y>=cellHeight7| Y<1.6f*rectWidth
            paint.setColor(Color.YELLOW);
            canvas.drawRect(1.6f*rectHeight, cellHeight7,2.6f*rectHeight, 1.6f*rectWidth, paint);//01  X>=1.6f*rectHeight | X<2.6f*rectHeight| Y>=cellHeight7 | Y< 1.6f*rectWidth
            paint.setColor(Color.BLUE);
            canvas.drawRect(2.6f*rectHeight, cellHeight7,6*(cellWidth7), 1.6f*rectWidth, paint);//02   X>=2.6f*rectHeight| X <6*(cellWidth7) | Y>=cellHeight7 | Y< 1.6f*rectWidth

            paint.setColor(Color.RED);
            canvas.drawRect(cellWidth7 +3f,1.6f*rectWidth ,1.6f*rectHeight, 2.6f*rectWidth, paint);//10 X>=cellWidth7 +3f| X <1.6f*rectHeight | Y>=1.6f*rectWidth | Y< 2.6f*rectWidth
            paint.setColor(Color.WHITE);
            canvas.drawRect(1.6f*rectHeight,1.6f*rectWidth ,2.6f*rectHeight, 2.6f*rectWidth,paint);//11 X>=1.6f*rectHeight| X <2.6f*rectHeight | Y>=1.6f*rectWidth | Y< 2.6f*rectWidth
            paint.setColor(Color.CYAN);
            canvas.drawRect(2.6f*rectHeight,1.6f*rectWidth ,6*(cellWidth7), 2.6f*rectWidth,paint);//12 X>=2.6f*rectHeight +3f| X <=6*(cellWidth7) | Y>=1.6f*rectWidth | Y< 2.6f*rectWidth

            //Paint paint = new Paint();
            paint.setColor(Color.GRAY);
            canvas.drawRect(cellWidth7 +3f,2.6f*rectWidth ,1.6f*rectHeight, 6*(cellWidth7) +5f,paint);//20 X>=cellWidth7 +3f  | X<1.6f*rectHeight | Y>=2.6f*rectWidth | Y<6*(cellWidth7)
            paint.setColor(Color.MAGENTA);
            canvas.drawRect(1.6f*rectHeight,2.6f*rectWidth ,2.6f*rectHeight, 6*(cellWidth7) +5f,paint);//20  X>=1.6f*rectHeight | X<2.6f*rectHeight | Y>=2.6f*rectWidth | Y<6*(cellWidth7)
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(2.6f*rectHeight,2.6f*rectWidth ,6*(cellWidth7), 6*(cellWidth7) +5f,paint);//20  X>=2.6f*rectHeight | X<=6*(cellWidth7) | Y>=2.6f*rectWidth | Y<=6*(cellWidth7)

            try{

                outputStream = openFileOutput("Applog",Context.MODE_PRIVATE);
                outputStream.write("start of log file".getBytes());
                //String path = Context.getFilesDir().getAbsolutePath();
                System.out.println("file opened");
                //outputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                //set time in mili
                Thread.sleep(3000);

            }catch (Exception e){
                e.printStackTrace();
            }
            tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status != TextToSpeech.ERROR) {
                        tts.setLanguage(Locale.UK);
                        Random rand = new Random();
                        String toSpeak = "Tap once on the watch to start";
                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                        tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
                    }
                }
            });
            i=1;
        }
        //PASTE HERE
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            map.put(0,"north west");
            map.put(1,"north");
            map.put(2,"north east");
            map.put(3,"west");
            map.put(4,"center");
            map.put(5,"east");
            map.put(6,"south west");
            map.put(7,"south");
            map.put(8,"south east");
            map.put(9,"center");
            if(i!=1) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.UK);
                            String toSpeak = " " + map.get(direction);
                            String fileinput="DIRECTION : "+ map.get(direction);
                            if(repeatVar==1)
                            {
                                toSpeak = "wrong, try again, click on "+ map.get(direction) ;
                                fileinput+=" WRONG USER INPUT ";
                            }
                            if(repeatVar==0)
                            {
                                Random rand = new Random();
                                direction=rand.nextInt(10);
                                toSpeak = "correct!     Click on " + map.get(direction);
                                fileinput+=" CORRECT USER INPUT ";
                            }
                            Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                            int var=tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null, null);
                            Date currentTimeTemp = Calendar.getInstance().getTime();
                            String input = "   " + currentTimeTemp +"   "+fileinput+ "\n";
                            try {
                                outputStream.write(input.getBytes());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }
//            tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                @Override
//                public void onInit(int status) {
//                    if(status != TextToSpeech.ERROR) {
//                        tts.setLanguage(Locale.UK);
//                        Random rand = new Random();
//                        int directionNumber=rand.nextInt(10);
//                        String toSpeak = "Click on "+map.get(directionNumber);
//                        //color=new StringBuffer();
//                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
//                        tts.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null,null);
//                    }
//                }
//            });
                try {
                    //set time in mili
                    Thread.sleep(70);

                }catch (Exception e){
                    e.printStackTrace();
                }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float row = event.getX();
                float col = event.getY();
                Date currentTime = Calendar.getInstance().getTime();
                if (i == 1) {
                    i = 2;
                    //System.out.println("-iiiii-------"+i);
                } else {
                    if(row < cellWidth7 + 3f || row > 6 * (cellWidth7) ||  col < cellHeight7 || col > 6 * (cellWidth7)  )
                    {
                        repeatVar=1;
                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                if (status != TextToSpeech.ERROR) {
                                    tts.setLanguage(Locale.UK);
                                    //String toSpeak = "Wrong, try again";
                                    String toSpeak = " ";
                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

                                }
                            }
                        });

                    }
                    else if (row >= cellWidth7 + 3f && row < 1.6f * rectHeight && col >= cellHeight7 && col < 1.6f * rectWidth) {
                        if("north west".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });

                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        String input = "GREEN   " + currentTime + "\n";
//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                    }
                    else if (row >= 1.6f * rectHeight && row < 2.6f * rectHeight && col >= cellHeight7 && col < 1.6f * rectWidth) {
                        System.out.println("YELLOW " + currentTime);
                        String input = "YELLOW   " + currentTime + "\n";
                        if("north".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });

                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }


//                        try {
//                            System.out.println("INSIDE YELLOW " + currentTime);
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "North";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= 2.6f * rectHeight && row <= 6 * (cellWidth7) && col >= cellHeight7 && col < 1.6f * rectWidth) {
                        System.out.println("BLUE " + currentTime);
                        String input = "BLUE   " + currentTime + "\n";
                        if("north east".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "North east";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= cellWidth7 + 3f && row < 1.6f * rectHeight && col >= 1.6f * rectWidth && col < 2.6f * rectWidth) {
                        System.out.println("RED " + currentTime);
                        String input = "RED   " + currentTime + "\n";
                        if("west".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "west";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                        String myData = "";
                        File myExternalFile = new File("/data/user/0/edu.cs4730.wearapp/files/", "Applog");
                        try {
                            FileInputStream fis = new FileInputStream(myExternalFile);
                            DataInputStream in = new DataInputStream(fis);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));

                            String strLine;
                            while ((strLine = br.readLine()) != null) {
                                myData = myData + strLine + "\n";
                            }
                            br.close();
                            in.close();
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("**************************" + myData);
                    }
                    else if (row >= 1.6f * rectHeight && row < 2.6f * rectHeight && col >= 1.6f * rectWidth && col < 2.6f * rectWidth) {
                        System.out.println("WHITE");
                        String input = "WHITE   " + currentTime + "\n";
                        if("center".equals(map.get(direction)))
                        {
                            repeatVar= 0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "Current location";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= 2.6f * rectHeight + 3f && row <= 6 * (cellWidth7) && col >= 1.6f * rectWidth && col < 2.6f * rectWidth) {
                        System.out.println("CYAN");
                        String input = "CYAN   " + currentTime + "\n";
                        if("east".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "east";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= cellWidth7 + 3f && row < 1.6f * rectHeight && col >= 2.6f * rectWidth && col <= 6 * (cellWidth7)) {
                        System.out.println("GRAY");
                        String input = "GRAY   " + currentTime + "\n";
                        if("south west".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "south west";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= 1.6f * rectHeight && row < 2.6f * rectHeight && col >= 2.6f * rectWidth && col <= 6 * (cellWidth7)) {
                        System.out.println("MAGENTA");
                        String input = "MAGENTA   " + currentTime + "\n";
                        if("south".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });

                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "south";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });
                    }
                    else if (row >= 2.6f * rectHeight && row <= 6 * (cellWidth7) && col >= 2.6f * rectWidth && col <= 6 * (cellWidth7)) {
                        System.out.println("LIGHTGRAY");
                        String input = "LIGHTGRAY   " + currentTime + "\n";
                        if("south east".equals(map.get(direction)))
                        {
                            repeatVar=0;
                            correctVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });
                        }
                        else
                        {
                            repeatVar=1;
                            tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {
                                    if (status != TextToSpeech.ERROR) {
                                        tts.setLanguage(Locale.UK);
                                        //String toSpeak = "Wrong, try again";
                                        String toSpeak = " ";
                                        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT);
                                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                                    }
                                }
                            });

                        }

//                        try {
//                            outputStream.write(input.getBytes());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                            @Override
//                            public void onInit(int status) {
//                                if (status != TextToSpeech.ERROR) {
//                                    tts.setLanguage(Locale.UK);
//                                    String toSpeak = "south east";
//                                    Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
//                                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                                }
//                            }
//                        });

                    }
                    System.out.println("GRID VALUES" + row + "Y values" + col);
                }
            }

            return true;
        }



    }

}
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Get the application context
//        mContext = getApplicationContext();
//
//        // Get the Resources
//        mResources = getResources();
//
//        // Get the widgets reference from XML layout
//        mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
//        mButton = (Button) findViewById(R.id.btn);
//        mImageView = (ImageView) findViewById(R.id.iv);
//
//        // Set a click listener for Button widget
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Initialize a new Bitmap object
//                Bitmap bitmap = Bitmap.createBitmap(
//                        500, // Width
//                        300, // Height
//                        Bitmap.Config.ARGB_8888 // Config
//                );
//
//                // Initialize a new Canvas instance
//                Canvas canvas = new Canvas(bitmap);
//
//                // Draw a solid color to the canvas background
//                canvas.drawColor(Color.LTGRAY);
//
//                // Initialize a new Paint instance to draw the Rectangle
//                Paint paint = new Paint();
//                paint.setStyle(Paint.Style.FILL);
//                paint.setColor(Color.YELLOW);
//                paint.setAntiAlias(true);
//
//                // Set a pixels value to padding around the rectangle
//                int padding = 50;
//
//                /*
//                    public Rect (int left, int top, int right, int bottom)
//                        Create a new rectangle with the specified coordinates. Note: no range
//                        checking is performed, so the caller must ensure that left <= right and
//                        top <= bottom.
//
//                    Parameters
//                        left : The X coordinate of the left side of the rectangle
//                        top : The Y coordinate of the top of the rectangle
//                        right : The X coordinate of the right side of the rectangle
//                        bottom : The Y coordinate of the bottom of the rectangle
//
//                */
//
//                // Initialize a new Rect object
//                Rect rectangle = new Rect(
//                        padding, // Left
//                        padding, // Top
//                        canvas.getWidth() - padding, // Right
//                        canvas.getHeight() - padding // Bottom
//                );
//
//                /*
//                    public void drawRect (Rect r, Paint paint)
//                        Draw the specified Rect using the specified Paint. The rectangle will be
//                        filled or framed based on the Style in the paint.
//
//                    Parameters
//                        r : The rectangle to be drawn.
//                        paint : The paint used to draw the rectangle
//                */
//
//                // Finally, draw the rectangle on the canvas
//                canvas.drawRect(rectangle,paint);
//
//                // Display the newly created bitmap on app interface
//                mImageView.setImageBitmap(bitmap);
//            }
//        });
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        setContentView(new CustomView(this));
////        mTextView = (TextView) findViewById(R.id.text);
////        mTextView.setText("   " + myRandom.nextInt(10) + " ");
////        //get the imagebutton (checkmark) and set up the listener for a random number.
////        ib = (ImageButton) findViewById(R.id.myButton);
////        ib.setOnClickListener(new View.OnClickListener() {
////
////            @Override
////            public void onClick(View v) {
////                mTextView.setText("   " + myRandom.nextInt(10) + " ");
////
////            }
////        });
//
//
//        // Enables Always-on
//        setAmbientEnabled();
//    }
//    protected void onDraw(Canvas canvas) {
//        radius = dip2px(getContext(), 150);//252
//        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//        canvas.save();
//        canvas.translate(getWidth() / 2, getHeight() / 2);
//        for (int i = -35; i <= 215; i += 1) {
//            drawOuterLine(i, canvas);
//        }
//        canvas.restore();
//    }

