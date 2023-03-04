package com.example.licenta.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.MedicsListActivity;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class APICommunication {
    public static JSONObject currentOBJ;
    public static JSONArray mediciArray;
    public static JSONArray appointmentsArray;
    public static JSONArray investigationsArray;
    public static String encodedImage;
    public static boolean invalidAppointment = false;
    private final static String APIURL = "http://192.168.100.75:8080/api";
    //    private final static String APIURL = "http://172.20.10.3:8080/api";
    private static StorageReference storageReference;

    public static void encodeFileBase64(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                encodedImage = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    pacient.setPhoto(uri.toString());
                    Log.i("url out", uri.toString());
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ctx, "Error occured!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void postPicture(Bitmap file, Context ctx) throws IOException {
        File f = new File(ctx.getCacheDir(), "temp");
        f.createNewFile();

//Convert bitmap to byte array
        Bitmap bitmap = file;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        encodeFileBase64(f);

        Map<String, String> mappedValues = new HashMap<>();
        mappedValues.put("encoded", encodedImage);
        Log.i("image", encodedImage);
        JSONObject obj4Send = new JSONObject(mappedValues);
        Log.i("jsonObj", obj4Send.toString());
//        try {
//            obj4Send.put("encoded1", encodedImage.substring(0,encodedImage.length()/3));
//            obj4Send.put("encoded2",encodedImage.substring(encodedImage.length()/3,2*encodedImage.length()/3));
//            obj4Send.put("encoded3",encodedImage.substring(2*encodedImage.length()/3));
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET,
                APIURL + "/decodeFile",
                obj4Send,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                            Log.i("Volley:", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                            Log.e("Volley:", error.getMessage());
                    }
                });
        queue.add(jsReq);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    public static void postPacient(Pacient pacient, Context ctx) {
        JSONObject obj4Send = new JSONObject();
        try {
            obj4Send.put("id", pacient.getId());
            obj4Send.put("firstName", pacient.getFirstName());
            obj4Send.put("lastName", pacient.getLastName());
            obj4Send.put("email", pacient.getEmail());
            obj4Send.put("cnp", pacient.getCNP());
            obj4Send.put("varsta", pacient.getVarsta());
            obj4Send.put("adresa", pacient.getAdresa());
            obj4Send.put("grad_urgenta", pacient.getGrad_urgenta());
            RequestQueue queue = Volley.newRequestQueue(ctx);
            JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST,
                    APIURL + "/addPacient",
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

    public static void putPacient(Map<String, Object> modifyAttributes, Context ctx) {
        JSONObject obj4Send = new JSONObject(modifyAttributes);
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.PUT,
                APIURL + "/modifyPacient",
                obj4Send,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VolleyPutPacient:", response.toString());
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

    public static void postProgramare(Programare prog, Pacient pac, Context ctx) {
        JSONObject obj4Send = new JSONObject();
        try {
            obj4Send.put("idPac", pac.getId());
            obj4Send.put("idMedic", prog.getMedic().getId());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                obj4Send.put("data", prog.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            obj4Send.put("observatii", "n/a");
            RequestQueue queue = Volley.newRequestQueue(ctx);
            JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST,
                    APIURL + "/addProgramare",
                    obj4Send,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("VolleyPostProg:", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("VolleyErrPostProg:", error.toString());
                        }
                    });
            queue.add(jsReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                            assert response != null;
                            Log.i("VolleyPing", response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            invalidAppointment = false;
                            Log.e("VolleyPING:", error.toString());
                        }
                    });
        }
        queue.add(jsReq);

    }

    public static void getPacient(String id, Context ctx) {
        Pacient p = null;
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET,
                APIURL + "/getPacient?id=" + id,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        currentOBJ = response;
                        Log.i("RESP", currentOBJ.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley:", error.toString());
                    }
                });
        queue.add(jsReq);
//        if (currentOBJ != null) {
//            p = new Pacient();
//            try {
//                p.setId(currentOBJ.getString("id"));
//                p.setFirstName(currentOBJ.getString("firstName"));
//                p.setLastName(currentOBJ.getString("lastName"));
//                p.setEmail(currentOBJ.getString("email"));
//                p.setVarsta(currentOBJ.getInt("varsta"));
//                p.setAdresa(currentOBJ.getString("adresa"));
//                p.setPhoto(currentOBJ.getString("photo"));
//                p.setGrad_urgenta(currentOBJ.getInt("grad_urgenta"));
//                p.setCNP(currentOBJ.getString("cnp"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return p;
    }

    public static void getAppointments(String idPac, Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/getProgramare?idPac=" + idPac,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        appointmentsArray = response;
                        Log.i("RESP_APP:", appointmentsArray.toString());
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

    public static void getMedics(Context ctx) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/allMedic",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mediciArray = response;
                        Log.i("RESP_MEDICI", mediciArray.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley:", error.toString());
            }
        });
        queue.add(jsReq);
    }

    public static void getInvestigations(Context ctx){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/getInvestigatii",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        investigationsArray = response;
                        Intent intent = new Intent("apiMessageInvestigation");
                        intent.putExtra("success", true);
                        LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsReq);
    }
}
