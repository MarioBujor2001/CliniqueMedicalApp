package com.example.licenta.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.Models.Investigation;
import com.example.licenta.Models.Order;
import com.example.licenta.Models.Pacient;
import com.example.licenta.Models.Programare;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class APICommunicationV2 {
    public static JSONObject currentOBJ;
    public static JSONArray mediciArray;
    public static JSONArray appointmentsArray;
    public static JSONArray investigationsArray;
    public static JSONArray ordersArray;
    public static JSONArray forumPostsArray;
    public static String encodedImage;
    public static boolean invalidAppointment = false;
    private final static String APIURL = "http://192.168.100.75:8080/api";
    //        private final static String APIURL = "http://172.20.10.3:8080/api";
    private static StorageReference storageReference;

    public static void uploadPictureFirebase(Uri image, Context ctx, Pacient pacient) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss", Locale.getDefault());
        Date now = new Date();
        String fileName = pacient.getId() + "_" + dateFormatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        String returnLink = "";

        storageReference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ctx, "Succesfully uploaded!", Toast.LENGTH_LONG).show();
                sendIntent("finishedUploadingPicture", true, ctx);
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    pacient.setPhoto(uri.toString());
                    Log.i("url out", uri.toString());
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, "Error occured!", Toast.LENGTH_LONG).show();
                sendIntent("finishedUploadingPicture", false, ctx);
            }
        });
    }


    //MODIFIED
    public static void postPatient(Pacient pacient, Context ctx) {
        JSONObject obj4Send = new JSONObject();
        try {
            obj4Send.put("id", pacient.getId());
            obj4Send.put("firstName", pacient.getFirstName());
            obj4Send.put("lastName", pacient.getLastName());
            obj4Send.put("email", pacient.getEmail());
            obj4Send.put("cnp", pacient.getCNP());
            obj4Send.put("age", pacient.getVarsta());
            obj4Send.put("address", pacient.getAdresa());
            RequestQueue queue = Volley.newRequestQueue(ctx);
            JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST,
                    APIURL + "/patients",
                    obj4Send,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("Volley:", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley:", error.getMessage());
                        }
                    });
            queue.add(jsReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //MODIFIED
    public static void putPatient(Map<String, Object> modifyAttributes, Context ctx) {
        JSONObject obj4Send = new JSONObject(modifyAttributes);
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.PUT,
                APIURL + "/patients/" + modifyAttributes.get("id"),
                obj4Send,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VolleyPutPacient:", response.toString());
                        sendIntent("receivedNewProfileInfo", true, ctx);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyPutPacientErr:", error.toString());
                    }
                });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void postAppointment(Programare prog, Pacient pac, Context ctx) {
        JSONObject obj4Send = new JSONObject();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                obj4Send.put("date", prog.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            obj4Send.put("comments", prog.getObservatii());
            obj4Send.put("medic", prog.getMedic());
            RequestQueue queue = Volley.newRequestQueue(ctx);
            JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST,
                    APIURL + "/patients/" + pac.getId() + "/appointments",
                    obj4Send,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("VolleyPostProg:", response.toString());
                            sendIntent("apiMessageSuccessReservation", true, ctx);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //va intra aici deoarece nu primi nimic inapoi
                            Log.i("VolleyErrPostProg:", error.toString());
                            sendIntent("apiMessageSuccessReservation", true, ctx);
                        }
                    });
            queue.add(jsReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //MODIFIED
    public static void deleteAppointment(Programare prog, String pacId, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.DELETE,
                APIURL + "/appointments/" + prog.getId(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (pacId != null) {
                            APICommunication.getAppointments(pacId, ctx);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
    }


    public static void pingProgramare(Programare prog, Pacient pac, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            jsReq = new JsonObjectRequest(Request.Method.GET,
                    APIURL + "/findProgramareByUID?data=" + prog.getData().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")) + "&idMedic=" + prog.getMedic().getId(),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            invalidAppointment = true;
                            sendIntent("apiMessageReservation", true, ctx);
                            assert response != null;
                            Log.i("VolleyPing", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            invalidAppointment = false;
                            sendIntent("apiMessageReservation", true, ctx);
                            Log.e("VolleyPING:", error.toString());
                        }
                    });
        }
        queue.add(jsReq);

    }

    //MODIFIED
    public static void getPacient(String id, Context ctx) {
        Pacient p = null;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET,
                APIURL + "/patients/" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        currentOBJ = response;
                        sendIntent("apiMessagePersonalInfo", true, ctx);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void getAppointments(String idPac, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/appointments?idPac=" + idPac,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        appointmentsArray = response;
                        sendIntent("apiMessageAppointments", true, ctx);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void getMedics(Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/medics",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mediciArray = response;
                        sendIntent("apiMessageMedics", true, ctx);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley:", error.toString());
            }
        });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void getInvestigations(Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/investigations",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        investigationsArray = response;
                        sendIntent("apiMessageInvestigation", true, ctx);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void postOrder(Context ctx, Pacient pac, List<Investigation> investigations) {
        JSONObject obj4Send = new JSONObject();
        Order order = new Order();
        order.setInvestigations(investigations);
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST,
                APIURL + "/patients/" + pac.getId() + "/orders",
                obj4Send,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VolleyPostProg:", response.toString());
                        sendIntent("apiMessageOrderCreate", true, ctx);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //va intra aici deoarece nu primi nimic inapoi
                        Log.i("VolleyErrPostProg:", error.toString());
                    }
                });
        queue.add(jsReq);

    }

    //MODIFIED
    public static void getOrders(Context ctx, Pacient pac) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/orders?idPac=" + pac.getId(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ordersArray = response;
                        sendIntent("apiMessageOrdersReceived", true, ctx);
                        Log.i("ordersCount", String.valueOf(appointmentsArray.length()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
    }

    //MODIFIED
    public static void getForumPosts(Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/forum-posts",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        forumPostsArray = response;
                        sendIntent("apiMessageForumPostsReceived", true, ctx);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
    }

    public static void sendIntent(String action, boolean isSuccessful, Context context) {
        Intent intent = new Intent(action);
        intent.putExtra("success", isSuccessful);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
