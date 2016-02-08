package com.archer.box_d_ball.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.archer.box_d_ball.Helper;
import com.archer.box_d_ball.MainActivity;
import com.archer.box_d_ball.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Middle layer between main activity & util helper classes for In App Billing Logic
 */
public class InAppBilling {

    MainActivity mContext;
    public IabHelper mHelper;
    String SKU_REMOVE_ADS = "sku_remove_ads";
    String RemoveAdsPrice;

    boolean connectedToInAppBillingServices;

    public InAppBilling(MainActivity m_context) {
        mContext = m_context;
        RemoveAdsPrice="";

        //region Check for Ad-Free version
        String AD_FREE_FILE = "jingalala_version";
        String data = "Yay! I am ad Free. ho-ho-ho.";
        if (Helper.readFromFile(new WeakReference<>(mContext), AD_FREE_FILE).equals(data))
        {
            mContext.adFreeVersion = true;
            mContext.hideMainAdView();
        }
        //endregion
    }

    public void initializeInAppBilling() {
        String base64EncodedPublicKey ;
        // compute your public key and store it in base64EncodedPublicKey
        base64EncodedPublicKey = getPublicKey();

        mHelper = new IabHelper(mContext, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                } else {
                    connectedToInAppBillingServices = true;
                    //check for pending consumptions
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                    getPriceInLocalCurrency();
                }
            }
        });
    }

    //region Check For pending items to consume
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener()
    {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            try
            {
                if (result.isFailure()) {
                    // handle error here
                }
                else {
                    Purchase purchase;
                    //consume pending purchases
                    if(!mContext.adFreeVersion)
                    {
                        purchase = inventory.getPurchase(SKU_REMOVE_ADS);

                        if (purchase != null && verifyDeveloperPayload(purchase))
                        {
                            mContext.adFreeVersion = true;
                            mContext.hideMainAdView();
                            String AD_FREE_FILE = "jingalala_version";
                            String data = "Yay! I am ad Free. ho-ho-ho.";
                            Helper.writeToFile(new WeakReference<>(mContext), AD_FREE_FILE, data);
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                 Toast.makeText(mContext,
                         "Error : Could not consume purchased item." + "\n" +
                         "\n" + "Please : " +
                         "\n" + "1. Restart 2 cards" +
                         "\n" + "2. Check internet connection" +
                         "\n" + "3. Contact us if issue persists",
                         Toast.LENGTH_SHORT).show();
            }
        }
    };
    //endregion


    //region get price in local currency
    public void getPriceInLocalCurrency() {
        if (!connectedToInAppBillingServices)
            return;

        IabHelper.QueryInventoryFinishedListener
                mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // handle error
                    return;
                }

                try {
                    RemoveAdsPrice = inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice();
                } catch (Exception ex) {
                    Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        List<String> additionalSkuList = new ArrayList<>();
        additionalSkuList.add(SKU_REMOVE_ADS);
        mHelper.queryInventoryAsync(true, additionalSkuList,
                mQueryFinishedListener);
    }
    //endregion


    public void LaunchPurchaseFlow(String SKU_ID)
    {
        if(!connectedToInAppBillingServices)
        {
            Toast.makeText(mContext,"Oops. Connection failed.\nPlease try after sometime.",Toast.LENGTH_SHORT).show();
            return;
        }

        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                try {

                    mHelper.flagEndAsync();
                if (result.isFailure()) {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                    Toast.makeText(mContext,"Purchased Failed\nPlease try after sometime",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (purchase.getSku().equals(SKU_REMOVE_ADS))
                {
                    mContext.adFreeVersion = true;
                    String AD_FREE_FILE = "jingalala_version";
                    String data = "Yay! I am ad Free. ho-ho-ho.";
                    Helper.writeToFile(new WeakReference<>(mContext),AD_FREE_FILE,data);
                    Toast.makeText(mContext,"Application upgraded successfully." +
                    "\nPlease give us a moment to refresh things!",Toast.LENGTH_SHORT).show();
                }
                }
                catch (Exception ex)
                {
                    Toast.makeText(mContext,ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };

        String code = "box_d_ball";
        int transactionID = code.hashCode();
        mHelper.launchPurchaseFlow(mContext, SKU_ID, transactionID,
                mPurchaseFinishedListener, jumble("69"));
    }

    public void setRemoveAdsPrice(View v)
    {
        try {
            if(!RemoveAdsPrice.equals(""))
                ((TextView)v).setText("At " + RemoveAdsPrice);
        }
        catch (Exception ex){ /* Do Nothing */ }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {

        String payload = p.getDeveloperPayload();
        return payload.trim().equals(jumble("619"));
    }

    public String jumble(String x)
    {
        String a,b,c,d;
        a="SwaS"; b="RiShi_NeVeR"; c="GiVe"; d="Up";
        x= a+"."+b+"."+c+"."+d;
        char y[] = x.toCharArray();
        int []xx = {3,9,5,7,2,11,3,9,5,7,2,11,};
        int l1 = xx.length;
        int l2 = y.length;
        for (int xxx : xx) {
            int index = xxx;
            for (int j = 0; j < l2; j++) {
                char yy = y[j];
                y[j] = y[index % l2];
                y[index % l2] = yy;
                index += xxx;
            }
        }
        x=String.valueOf(y);
        return x.trim();
    }

    public String getPublicKey()
    {
        String key[]=
                {
                        "o0djwAEQACKgCBIIMA8QACOAAFEQAB0w9GikhqkgBNAjIBIIM",
                        "GBH+FEefhHKc1ODRh4TxohISREC+1tn6/gdi/2nN30oT",
                        "pLowSDuW4m6ieHw/retg86/MYRwAgh9l3HaZd6tSEiZ4vZZN",
                        "LnbYuo/Wi/DJMQHzrIUQa04a9nGMh2FPfhfDDozBPi/b+",
                        "3eLG3YSDBIpynxZE6BTRDaAQb+l6ca7ymxy12KIeSFPv",
                        "aI6aLP+s0mW3TkPaLhMl9tTdpR3vLYOjCWmkkf",
                        "NUJSZ1nAxygv40m7T4MBO4JDY8M8H+ky0zvK4u",
                        "/iViob9HNqpAAGCweJHWj5Kq6/Rpa5J6NECm",
                        "r49VPCvAT89+juK0pYexZ3sY67wW06r",
                        "/1aYmjevkKj8QIDAQAB"
                };

        String final_result="";
        int length= key.length;
        for(int i=0;i<length;i++)
        {
            if(i%2==0)
                final_result+= new StringBuilder(key[i]).reverse().toString();
            else
                final_result+= key[i];
        }

        return final_result.trim();
    }

}
