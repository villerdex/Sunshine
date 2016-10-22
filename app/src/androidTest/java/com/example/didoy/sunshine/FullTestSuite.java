package com.example.didoy.sunshine;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by Didoy on 10/8/2016.
 */

public class FullTestSuite extends TestSuite {

        public static Test suite(){
            return new TestSuiteBuilder(FullTestSuite.class)
                    .includeAllPackagesUnderHere().build();
        }


    public FullTestSuite(){
        super();
    }
}
