package com.example.licenta.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.licenta.Models.Pacient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class APICommunication {
    public static JSONObject currentOBJ;
    public static JSONArray mediciArray;
    private final static String APIURL = "http://192.168.100.75:8080/api";

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
                        Log.i("RESP",currentOBJ.toString());
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

    public static void getMedics(Context ctx){
        RequestQueue queue = Volley.newRequestQueue(ctx);
        JsonArrayRequest jsReq = new JsonArrayRequest(Request.Method.GET,
                APIURL + "/allMedic",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mediciArray = response;
                        Log.i("RESP_MEDICI",mediciArray.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley:", error.toString());
            }
        });
        queue.add(jsReq);
    }
}