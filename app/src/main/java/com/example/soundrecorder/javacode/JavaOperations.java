package com.example.soundrecorder.javacode;

import android.util.Log;

import static java.lang.Math.log10;

public class JavaOperations {
    private static double highestDecibel = 0;
    private static double frequencyCorrespondingToHeightstDecibel = 0;
    private static double time = 0;

    //convert short to byte
    public byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;
    }

    public int calculate(int sampleRate, short [] audioData){
        double db = 0;
        for (int i = 0 ; i < audioData.length ; i++){
            /*Log.i(TAG, "calculate: num is "+ (double) (20 * log10(audioData[i])));*/
            if(db < (double) (20 * log10(audioData[i])))
            {
                db = (double) (20 * log10(audioData[i]));
            }
        }

       /* i++;
        Log.i(TAG, "calculate: i "+i);*/
        int numSamples = audioData.length;
        int numCrossing = 0;
        for (int p = 0; p < numSamples-1; p++)
        {
            if ((audioData[p] > 0 && audioData[p + 1] <= 0) || (audioData[p] < 0 && audioData[p + 1] >= 0))
            {
                numCrossing++;
            }
        }

        float numSecondsRecorded = (float)numSamples/(float)sampleRate;
        Log.i(TAG, "calculate: milis seconde is "+numSecondsRecorded);
        float numCycles = numCrossing/2;
        float frequency = numCycles/numSecondsRecorded;
        if(highestDecibel < db)
        {
            highestDecibel =  db;
            frequencyCorrespondingToHeightstDecibel = frequency;
        }
        if(highestDecibel > 50)
        {
            Log.i(TAG, "calculate: highest decibel is "+highestDecibel+" and corresponding frequency is "+frequencyCorrespondingToHeightstDecibel);
        }
        Log.i(TAG, "calculate: milis seconde is "+numSecondsRecorded);
        // it is giving data in every 23 mili seconde and after totaling time it comes correct time
        time = time + numSecondsRecorded;
        Log.i(TAG, "calculate: total time is "+time);
        Log.i(TAG, "calculate: max decibel is "+db+" and frequency is "+frequency);
        return (int)frequency;
    }

    private static final String TAG = "JavaOperations";
}
