package com.example.yubin.gomoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.example.yubin.gomoku.Timer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Random;


/**
 * Created by jialuwang on 2/20/17.
 */


public class GameActivity extends AppCompatActivity {
    private int maxN;
    private Context context;
    private ImageView[][] ivCell;

    private Drawable[] drawCell = new Drawable[12];//0 is empty, 1 is player, 2 is robot, 3 is background

    private Button btnPlay;
    private TextView tvTurn;
    public TextView tvTimer1;
    public TextView tvTimer2;
    public TextView winner_record;
    private int[][] valueCell;
    private int winner_play;
    private boolean firstMove;
    private int xMove, yMove;  //position of a move
    private int turnPlay;      //whose turn?
    private Timer[] mytimers = new Timer[3];
    private boolean standardMode;
    private int playWith;

    // variables for bluetooth
    public boolean flag_host = false;
    public boolean flag_guest;

    // Local Bluetooth adapter

    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothManager mBluetoothManager = null;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private String pairedDeviceName;
    private boolean opponentReady = false;
    private boolean selfReady = false;
    private boolean can_move = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //    private PubNub pubnub;
    public GameActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // Read parameters passed from OptionPage
        Bundle b = getIntent().getExtras();

        standardMode = b.getBoolean("playStyle");
        maxN = b.getInt("boardSize");
        playWith = b.getInt("playWith");

        Log.i("in GameActivity, maxN:", Integer.toString(maxN));
        Log.i("in GameActivity,std:", Boolean.toString(standardMode));
        Log.i("in GameActivity, playw:", Integer.toString(playWith));

        // If the adapter is null, then Bluetooth is not supported


        if (playWith == 2) {
            //setHasOptionsMenu();
            // Get local Bluetooth adapter
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            /*PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey("sub-c-59b9bd6a-fdf2-11e6-8d8a-0619f8945a4f");
            pnConfiguration.setPublishKey("pub-c-23154573-a551-4064-84a8-bfcd4112c272");
            pnConfiguration.setSecure(false);

            pubnub = new PubNub(pnConfiguration);

            pubnub.subscribe()
                    .channels(Arrays.asList("my_channel")) // subscribe to channels
                    .execute();
                    Log.i("in subscribe", "finish");
            pubnub.addListener(new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {
                    if (status.getOperation() != null) {
                        switch (status.getOperation()) {
                            // let's combine unsubscribe and subscribe handling for ease of use
                            case PNSubscribeOperation:
                            case PNUnsubscribeOperation:
                                // note: subscribe statuses never have traditional
                                // errors, they just have categories to represent the
                                // different issues or successes that occur as part of subscribe
                                switch (status.getCategory()) {
                                    case PNConnectedCategory:
                                        // this is expected for a subscribe, this means there is no error or issue whatsoever
                                    case PNReconnectedCategory:
                                        // this usually occurs if subscribe temporarily fails but reconnects. This means
                                        // there was an error but there is no longer any issue
                                    case PNDisconnectedCategory:
                                        // this is the expected category for an unsubscribe. This means there
                                        // was no error in unsubscribing from everything
                                    case PNUnexpectedDisconnectCategory:
                                        // this is usually an issue with the internet connection, this is an error, handle appropriately
                                    case PNAccessDeniedCategory:
                                        // this means that PAM does allow this client to subscribe to this
                                        // channel and channel group configuration. This is another explicit error
                                    default:
                                        // More errors can be directly specified by creating explicit cases for other
                                        // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                                }

                            case PNHeartbeatOperation:
                                // heartbeat operations can in fact have errors, so it is important to check first for an error.
                                // For more information on how to configure heartbeat notifications through the status
                                // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                                if (status.isError()) {
                                    // There was an error with the heartbeat operation, handle here
                                } else {
                                    // heartbeat operation was successful
                                }
                            default: {
                                // Encountered unknown status type
                            }
                        }
                    } else {
                        // After a reconnection see status.getCategory()
                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    int mes = message.getMessage().getAsInt();

                    xMove = (mes >> 16) & 0xff;
                    yMove  = (mes >> 8) & 0xff;
                    turnPlay = mes & 0xff;
                    Log.i("in subscribe message", Integer.toString(xMove) + Integer.toString(yMove));
                    if(turnPlay != 0) {
                        //make_a_move();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                make_a_move();
                            }
                        });
                    }
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });*/
        }

        loadResources();
        designBoardGame();
        setListen();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setListen() {
        if (playWith != 0) {
            tvTimer1 = (TextView) findViewById(R.id.timerText1);
            tvTimer2 = (TextView) findViewById(R.id.timerText2);
        }

        btnPlay = (Button) findViewById(R.id.btnPlay);
        tvTurn = (TextView) findViewById(R.id.tvTurn);

        btnPlay.setText("New Game");
        tvTurn.setText("Click New Game to Start");

        if (playWith == 2) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                if (mBluetoothManager == null)
                    mBluetoothManager = new BluetoothManager(this, mHandler);
            }
        }
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playWith == 2) {
                    init_game();
                    selfReady = true;
                    sendMessage("ready");
                    if (opponentReady == true) {
                        play_game();
                        selfReady = false;
                        opponentReady = false;
                    }
                } else {
                    init_game();
                    play_game();
                }
            }
        });
    }

    private void play_game() {
        //define who play first
        //Random r = new Random();
        //turnPlay = r.nextInt(2) + 1; //chose one value between [0,1]
        turnPlay = 1;
        if (turnPlay == 1) {
            Toast.makeText(context, "Player1 plays first", Toast.LENGTH_SHORT).show();
            //player1Turn();
            playerTurn(1);
        } else {
            if (playWith == 0) {
                Toast.makeText(context, "AI plays first", Toast.LENGTH_SHORT).show();
                AITurn();
            } else {
                Toast.makeText(context, "Player2 plays first", Toast.LENGTH_SHORT).show();
                playerTurn(2);
            }
        }

    }

    private void AITurn() {
        tvTurn.setText("Robot");
        //Let AI's first move always in center cell
        if (firstMove) {
            firstMove = false;
            xMove = (maxN - 1) / 2;
            yMove = (maxN - 1) / 2;
            make_a_move();
        } else {
            //try to fins the best move
            findBotMove();
            make_a_move();
        }
    }

    private final int[] iRow = {-1, -1, -1, 0, 1, 1, 1, 0};
    private final int[] iCol = {-1, 0, 1, 1, 1, 0, -1, -1};

    private void findBotMove() {
        List<Integer> listX = new ArrayList<Integer>();
        List<Integer> listY = new ArrayList<Integer>();
        //find empty cell can move, and we we only move two cell in range 2
        final int range = 2;
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++)
                if (valueCell[i][j] != 0) {//not empty
                    for (int t = 1; t <= range; t++) {
                        for (int k = 0; k < 8; k++) {
                            int x = i + iRow[k] * t;
                            int y = j + iCol[k] * t;
                            if (inBoard(x, y) && valueCell[x][y] == 0) {
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
        }
        int lx = listX.get(0);
        int ly = listY.get(0);
        //bot always find min board_position_value
        int res = Integer.MAX_VALUE - 10;
        for (int i = 0; i < listX.size(); i++) {
            int x = listX.get(i);
            int y = listY.get(i);
            valueCell[x][y] = 2;
            int rr = getValue_Position();
            if (rr < res) {
                res = rr;
                lx = x;
                ly = y;
            }
            valueCell[x][y] = 0;
        }
        xMove = lx;
        yMove = ly;
    }

    private int getValue_Position() {
        //this function will find the board_position_value
        int rr = 0;
        int pl = turnPlay;
        //row
        for (int i = 0; i < maxN; i++) {
            rr += CheckValue(maxN - 1, i, -1, 0, pl);
        }
        //column
        for (int i = 0; i < maxN; i++) {
            rr += CheckValue(i, maxN - 1, 0, -1, pl);
        }
        //cross right to left
        for (int i = maxN - 1; i >= 0; i--) {
            rr += CheckValue(i, maxN - 1, -1, -1, pl);
        }
        for (int i = maxN - 2; i >= 0; i--) {
            rr += CheckValue(maxN - 1, i, -1, -1, pl);
        }
        //cross left to right
        for (int i = maxN - 1; i >= 0; i--) {
            rr += CheckValue(i, 0, -1, 1, pl);
        }
        for (int i = maxN - 1; i >= 1; i--) {
            rr += CheckValue(maxN - 1, i, -1, 1, pl);
        }
        return rr;
    }

    private int CheckValue(int xd, int yd, int vx, int vy, int pl) {
        //comback with check value
        int i, j;
        int rr = 0;
        i = xd;
        j = yd;
        String st = String.valueOf(valueCell[i][j]);
        while (true) {
            i += vx;
            j += vy;
            if (inBoard(i, j)) {
                st = st + String.valueOf(valueCell[i][j]);
                if (st.length() == 6) {
                    rr += Eval(st, pl);
                    st = st.substring(1, 6);
                }
            } else break;
        }
        return rr;
    }

    private int Eval(String st, int pl) {
        //this function is put score for 6 cells in a row
        //pl is player turn => you will get a bonus point if it's your turn
        //I will show you and explain how i can make it and what it mean in part improve bot move
        int b1 = 1, b2 = 1;
        if (pl == 1) {
            b1 = 2;
            b2 = 1;
        } else {
            b1 = 1;
            b2 = 2;
        }
        switch (st) {
            case "111110":
                return b1 * 100000000;
            case "011111":
                return b1 * 100000000;
            case "211111":
                return b1 * 100000000;
            case "111112":
                return b1 * 100000000;
            case "011110":
                return b1 * 10000000;
            case "101110":
                return b1 * 1002;
            case "011101":
                return b1 * 1002;
            case "011112":
                return b1 * 1000;
            case "011100":
                return b1 * 102;
            case "001110":
                return b1 * 102;
            case "210111":
                return b1 * 100;
            case "211110":
                return b1 * 100;
            case "211011":
                return b1 * 100;
            case "211101":
                return b1 * 100;
            case "010100":
                return b1 * 10;
            case "011000":
                return b1 * 10;
            case "001100":
                return b1 * 10;
            case "000110":
                return b1 * 10;
            case "211000":
                return b1 * 1;
            case "201100":
                return b1 * 1;
            case "200110":
                return b1 * 1;
            case "200011":
                return b1 * 1;
            case "222220":
                return b2 * -100000000;
            case "022222":
                return b2 * -100000000;
            case "122222":
                return b2 * -100000000;
            case "222221":
                return b2 * -100000000;
            case "022220":
                return b2 * -10000000;
            case "202220":
                return b2 * -1002;
            case "022202":
                return b2 * -1002;
            case "022221":
                return b2 * -1000;
            case "022200":
                return b2 * -102;
            case "002220":
                return b2 * -102;
            case "120222":
                return b2 * -100;
            case "122220":
                return b2 * -100;
            case "122022":
                return b2 * -100;
            case "122202":
                return b2 * -100;
            case "020200":
                return b2 * -10;
            case "022000":
                return b2 * -10;
            case "002200":
                return b2 * -10;
            case "000220":
                return b2 * -10;
            case "122000":
                return b2 * -1;
            case "102200":
                return b2 * -1;
            case "100220":
                return b2 * -1;
            case "100022":
                return b2 * -1;
            default:
                break;
        }
        return 0;
    }

    private void make_a_move() {
        Log.i("xMove = ", Integer.toString(xMove));
        Log.i("yMove = ", Integer.toString(yMove));
        Log.i("turnPlay = ", Integer.toString(turnPlay));

        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;

        //check if anyone win
        CheckWinner();
        if (winner_play != 0) {
            display_winner(winner_play);
            return;
        }
        turnPlay = 3 - turnPlay;

        if (turnPlay == 2 && playWith == 0) {
            AITurn();
        } else {
            playerTurn(turnPlay);
        }
    }

    private void playerTurn(int turn) {
        tvTurn.setText("Player " + turn);
        firstMove = false;
        isClicked = false;
        if (playWith != 0) {
            if (mytimers[turn].start() == 1) {
                winner_play = 3 - turn;
                display_winner(winner_play);
            }
            mytimers[3 - turn].pause();
        }
    }

    private void display_winner(int winner) {
        if (winner == 1) {
            Toast.makeText(context, "Winner is Player1", Toast.LENGTH_SHORT).show();//
            tvTurn.setText("Winner is Player1");
            winner_record = (TextView) findViewById(R.id.record);
            winner_record.append("Winner is Player1\n");
        } else if (winner == 2) {
            if (playWith == 0) {
                Toast.makeText(context, "Winner is AI", Toast.LENGTH_SHORT).show();//
                tvTurn.setText("Winner is AI");
                winner_record = (TextView) findViewById(R.id.record);
                winner_record.append("Winner is AI\n");
            } else {
                Toast.makeText(context, "Winner is Player2", Toast.LENGTH_SHORT).show();//
                tvTurn.setText("Winner is Player2");
                winner_record = (TextView) findViewById(R.id.record);
                winner_record.append("Winner is Player2\n");
            }
        } else {
            Toast.makeText(context, "Draw", Toast.LENGTH_SHORT).show();
        }

        if (playWith != 0) {
            mytimers[1].pause();
            mytimers[2].pause();
        }


    }

    private boolean CheckWinner() {
        //to check if 5 stones in a row
        Log.i("in CheckWinner", " ");
        if (winner_play != 0) return true;
        if (noEmptyCell()) {
            winner_play = 3;
        }
        VectorEnd(xMove, 1, 0, 1, xMove, yMove, standardMode);//check in row
        VectorEnd(1, yMove, 1, 0, xMove, yMove, standardMode);//check in column
        if (xMove + yMove >= maxN - 1) {
            VectorEnd(maxN - 1, xMove + yMove - maxN + 1, -1, 1, xMove, yMove, standardMode);
        } else {
            VectorEnd(xMove + yMove, 0, -1, 1, xMove, yMove, standardMode);
        }
        //check right to left
        if (xMove <= yMove) {
            VectorEnd(xMove - yMove + maxN - 1, maxN - 1, -1, -1, xMove, yMove, standardMode);
        } else {
            VectorEnd(maxN - 1, maxN - 1 - (xMove - yMove), -1, -1, xMove, yMove, standardMode);
        }
        if (winner_play != 0) return true;
        else return false;
    }

    private void VectorEnd(int xx, int yy, int vx, int vy, int rx, int ry, boolean standardStyle) {
        if (winner_play != 0) return;
        final int range = 4;
        int i, j, m, n;
        int xbelow = rx - range * vx;
        int ybelow = ry - range * vy;
        int xabove = rx + range * vx;
        int yabove = ry + range * vy;
        String st = "";
        String st_seven = "";
        i = xx;
        j = yy;
        while (!inside(i, xbelow, xabove) || !inside(j, ybelow, yabove)) {
            i += vx;
            j += vy;
        }
        while (true) {
            st = st + String.valueOf(valueCell[i][j]);
            if (st.length() == 5) {
                if (EvalEnd_five(st)) {
                    m = i - 5 * vx;
                    n = j - 5 * vy;
                    st_seven = String.valueOf(valueCell[m][n]) + st;
                    m = i + vx;
                    n = j + vy;
                    st_seven += String.valueOf(valueCell[m][n]);
                    if (standardMode) {
                        if ((!EvalEnd_seven(st_seven)) && (!EvalEnd_six(st_seven))) {
                            EvalEnd_winner(st_seven);
                            return;
                        }
                    } else {
                        if (!EvalEnd_seven(st_seven)) {
                            EvalEnd_winner(st_seven);
                            return;
                        }
                    }
                }
                st = st.substring(1, 5);//subsring of st from 1-5
            }
            i += vx;
            j += vy;
            if (!inBoard(i, j) || !inside(i, xbelow, xabove) || !inside(j, ybelow, yabove) || winner_play != 0) {
                break;
            }
        }
    }

    private boolean inBoard(int i, int j) {
        //to check i,j in broad or not
        if (i < 1 || i > maxN - 2 || j < 1 || j > maxN - 2) return false;
        return true;
    }

    private boolean EvalEnd_five(String st) {
        if (st.contains("11111") || st.contains("22222")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean EvalEnd_seven(String st) {
        if (st.contains("2111112") || st.contains("1222221"))
            return true;
        else
            return false;
    }

    private boolean EvalEnd_six(String st) {
        if (st.contains("111111") || st.contains("222222"))
            return true;
        else
            return false;
    }

    private void EvalEnd_winner(String st) {
        if (st.contains("11111"))
            winner_play = 1;
        else
            winner_play = 2;
    }

    private boolean inside(int i, int xbelow, int xabove) {
        return (i - xbelow) * (i - xabove) <= 0;
    }

    private boolean noEmptyCell() {
        for (int i = 1; i < maxN - 1; i++) {
            for (int j = 1; j < maxN - 1; j++) {
                if (valueCell[i][j] == 0) return false;
            }
        }
        return true;
    }


    private void init_game() {
        if (playWith != 0) {
            if (mytimers[1] != null) {
                mytimers[1].pause();
                mytimers[2].pause();
            }
            mytimers[1] = new Timer(tvTimer1);
            mytimers[2] = new Timer(tvTimer2);
        }
        firstMove = true;
        winner_play = 0;
        int i;
        int j;
        for (i = 1; i < maxN - 1; i++) {
            for (j = 1; j < maxN - 1; j++) {
                ivCell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j] = 0;
            }
        }
    }


    private void loadResources() {
        drawCell[0] = context.getResources().getDrawable(R.drawable.empty);
        drawCell[1] = context.getResources().getDrawable(R.drawable.blackstone);
        drawCell[2] = context.getResources().getDrawable(R.drawable.whitestone);
        drawCell[3] = context.getResources().getDrawable(R.drawable.topleft);
        drawCell[4] = context.getResources().getDrawable(R.drawable.topright);
        drawCell[5] = context.getResources().getDrawable(R.drawable.bottomleft);
        drawCell[6] = context.getResources().getDrawable(R.drawable.bottomright);
        drawCell[7] = context.getResources().getDrawable(R.drawable.top);
        drawCell[8] = context.getResources().getDrawable(R.drawable.left);
        drawCell[9] = context.getResources().getDrawable(R.drawable.bottom);
        drawCell[10] = context.getResources().getDrawable(R.drawable.right);
        drawCell[11] = null;
    }

    private boolean isClicked; //make sure player just clicks on one cell

    @SuppressLint("NewApi")
    private void designBoardGame() {
        int sizeofCell = Math.round(ScreenWidth() / maxN);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeofCell * maxN, sizeofCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeofCell, sizeofCell);

        LinearLayout linBoardgame = (LinearLayout) findViewById(R.id.linBoardGame);
        Log.i("in designBoardGame", Integer.toString(maxN));

        ivCell = new ImageView[maxN][maxN];
        valueCell = new int[maxN][maxN];

        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = new LinearLayout(context);
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j] = new ImageView(context);
                if (i == 0 && j == 0) {
                    ivCell[i][j].setBackground(drawCell[3]);
                } else if (i == 0 && j == maxN - 1) {
                    ivCell[i][j].setBackground(drawCell[4]);
                } else if (i == maxN - 1 && j == 0) {
                    ivCell[i][j].setBackground(drawCell[5]);
                } else if (i == maxN - 1 && j == maxN - 1) {
                    ivCell[i][j].setBackground(drawCell[6]);
                } else if (i == 0) {
                    ivCell[i][j].setBackground(drawCell[7]);
                } else if (j == 0) {
                    ivCell[i][j].setBackground(drawCell[8]);
                } else if (i == maxN - 1) {
                    ivCell[i][j].setBackground(drawCell[9]);
                } else if (j == maxN - 1) {
                    ivCell[i][j].setBackground(drawCell[10]);
                } else {
                    ivCell[i][j].setBackground(drawCell[0]);
                    final int x = i;
                    final int y = j;
                    ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (winner_play != 0) return;
                            if (playWith == 2 && can_move == false) return;
                            if (valueCell[x][y] == 0) {
                                if (turnPlay == 1 || !isClicked) { //turn of player
                                    isClicked = true;
                                    xMove = x;
                                    yMove = y;
                                    if (playWith == 2) {
                                        String message = xMove + " " + yMove + " " + turnPlay;
                                        sendMessage(message);
                                        Log.i("after sendmessage:", xMove + " " + yMove + " " + turnPlay);
                                        can_move = false;
                                       /* publishpubnub.publish()
                                                .message((xMove << 16) + (yMove << 8) + turnPlay)
                                                .channel("my_channel")
                                                .shouldStore(true)
                                                .usePOST(true)
                                                .async(new PNCallback<PNPublishResult>() {
                                                    @Override
                                                    public void onResponse(PNPublishResult result, PNStatus status) {
                                                        if (status.isError()) {
                                                            // something bad happened.
                                                            System.out.println("error happened while publishing: " + status.toString());
                                                        } else {
                                                            Log.i("in publish, i, j = ", "");

                                                            System.out.println("publish worked! timetoken: " + result.getTimetoken());
                                                        }
                                                    }
                                                });*/
                                    }
                                    make_a_move();
                                }
                            }
                        }
                    });
                }
                valueCell[i][j] = 0;

                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoardgame.addView(linRow, lpRow);
        }
    }

    private float ScreenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //report the state of connection between 2 devices
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothManager.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), R.string.status_connected_to, Toast.LENGTH_SHORT).show();
                            /*if (flag_host) {
                                Toast.makeText(getApplicationContext(), "You're host.", Toast.LENGTH_SHORT).show();

                                //Show your current Board

                                //Send your board information over to guest

                            } else
                                Toast.makeText(getApplicationContext(), "You're guest.", Toast.LENGTH_SHORT).show();*/
                            break;
                        case BluetoothManager.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(), R.string.status_connecting, Toast.LENGTH_SHORT).show();
                            //flag_host = true;
                            break;
                        case BluetoothManager.STATE_LISTEN:
                        case BluetoothManager.STATE_NONE:
                            Toast.makeText(getApplicationContext(), R.string.status_not_connected, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;

                // save the connected device's name
                case MESSAGE_DEVICE_NAME:
                    //do something
                    pairedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + pairedDeviceName, Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;

                //read a message from a bluetooth device
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String rawMessage = new String(readBuf, 0, msg.arg1);
                    String[] argTokens = rawMessage.split(" ");
                    Log.i("argToken0: ", argTokens[0]);
                    if (argTokens[0].equals("ready")) {
                        opponentReady = true;
                        if (selfReady) {
                            can_move = true;
                            play_game();
                            selfReady = false;
                            opponentReady = false;
                        }
                    } else {
                        xMove = Integer.valueOf(argTokens[0]);
                        yMove = Integer.valueOf(argTokens[1]);
                        turnPlay = Integer.valueOf(argTokens[2]);
                        make_a_move();
                        can_move = true;
                    }
                    /*switch (argTokens[0]) {
                        case NOTIFY_WORD_FOUND:
                            updateOpponentScore(argTokens);
                            break;

                        case SEND_BOARD_DATA:
                            receiveBoardData(argTokens);
                            break;

                        case END_GAME_GUEST:
                            endGameHost(argTokens);
                            break;

                        case PLAYER_STOPPED_TIMER:
                            opponentStoppedTimer(argTokens);
                            break;
                        case REQUEST_RESULTS_FROM_GUEST:
                            sendResults();
                    }*/
                    //do something
                    //Toast.makeText(getApplicationContext(), rawMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void connect(View v) {
        Intent serverIntent = new Intent(this, BluetoothDeviceList.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    public void discoverable(View v) {
        ensureDiscoverable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (playWith == 2) {
            System.out.println("in func onCreateOptionsMenu");
            getMenuInflater().inflate(R.menu.gomoku_menu, menu);
            return super.onCreateOptionsMenu(menu);
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, BluetoothDeviceList.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBluetoothManager.getState() != BluetoothManager.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBluetoothManager.write(send);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBluetoothManager.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == this.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mBluetoothManager = new BluetoothManager(this, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}