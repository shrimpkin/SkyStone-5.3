/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.List;

/**
 * This 2019-2020 OpMode illustrates the basics of using the TensorFlow Object Detection API to
 * determine the position of the Skystone game elements.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list.
 *
 * IMPORTANT: In order to use this OpMode, you need to obtain your own Vuforia license key as
 * is explained below.
 */
@TeleOp(name = "Detect skystone position", group = "Concept")
//@Disabled
public class ConceptTensorFlowObjectDetection extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Stone";
    private static final String LABEL_SECOND_ELEMENT = "Skystone";

    private double leftX = 0;
    private double stoneCX = 0;
    private  double screenCX = 0;

    private DcMotor frontLeftWheel;
    private DcMotor backLeftWheel;
    private DcMotor frontRightWheel;
    private DcMotor backRightWheel;


    //constants for adjusting robot moving based off of weight distribution
    private int flwChange = 1;
    private int blwChange = 1;
    private int frwChange = 1;
    private int brwChange = 1;

    private float front_left;
    private float rear_left;
    private float front_right;
    private float rear_right;

    private String log = "Fields Initialized";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY = "Abyd5Br/////AAABmRdvKE0dIk+VoE3dZUX80Qgvs0taq2LkrHwvZqfHxuRb/7H5kBamN+WGMyUKT/ECCRxHZ9umWluLjUXas7DxXSDi3BWN1FbACcFP9JllAwNAYwv98E0LTkAfd6Wqmg/xLsYUf2horiU5L+yPqaiho9MU/Kuan1Rv9YKZXgFHRneI84YbjsUuhLzM+yO63k1rQqFtUlSGZA64WI/lH/+simvahLPT+XbSeU8pCff3RB26LfRJUqscB3PE43E9+gdBqZ8S5wYChNKO3YDBf6wpGXhp7+Qj13X+7TC+CPykzyTHSShS1vGw8QMwbvZN3/WNLgkxV/o2bA3dbXCTrdXFNFO0wURB7o4YRxhTrj9q/2Jf ";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    @Override
    public void runOpMode() {

        //maps each dcMotor to the corresponding name listed on the "Configure Robot"
        frontLeftWheel = hardwareMap.dcMotor.get("frontLeft");
        backRightWheel = hardwareMap.dcMotor.get("backRight");
        frontRightWheel = hardwareMap.dcMotor.get("frontRight");
        backLeftWheel =  hardwareMap.dcMotor.get("backLeft");

        log = "hardwareMapped";
        telemetry.addData("Out put", log);


        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start op mode");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());


                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {

                            // [ some of the \telemetry lines are comment to reduce the information logged on the screen]
                            // telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            // telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f", recognition.getLeft(), recognition.getTop());
                            //telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f", recognition.getRight(), recognition.getBottom());
                            // telemetry.addData("Object Size", recognition.getHeight() + "'" + recognition.getWidth());

                            telemetry.addData("line break:"," --------------------------");

                            telemetry.addData("stone width", recognition.getWidth());//getting stone width
                            telemetry.addData("Image Height", recognition.getWidth());//getting image height
                            telemetry.addData("ImageWidth", recognition.getImageWidth()/2);//getting Image Height

                            /**
                             * Centering the Robot to the block/stone Explanation:
                             * We are no longer going to use the top left X of the stone to position the robot to the stone because
                             * when the robot gets closer to the stone we lose track of the  top left X and we would end up doing more work to make this work,
                             * so we are going to use the center X of the stone this will work better because its always going to be easy for the camera to see the center of the stone even if the robot is far or close to the stone
                             * the code below has been changed to work as mentioned above.
                             *
                             * NOTE: the code running below only logs "go left" or "go right" basing on the center X of the block[@var stoneCX] and the center X of the screen or Image[@var scree]
                             * we need to create a threshold that will tell if the stone is in center
                             * since we are going to use the logitech camera, we are going to use a different sample file
                             * that contain packages supporting extanernal camera usage [THIS FILE SAMPLE IS LOCATED IN FtcRobotController/org.firstinspires.ftc.robotcontroller/external.samples/ConceptTensorFlowObjectDetectionWebcom]
                             * */
                             //leftX = recognition.getLeft();// get the left X of the stone
                             stoneCX = (recognition.getRight() + recognition.getLeft())/2;//get center X of stone
                             screenCX = recognition.getImageWidth()/2; // get center X  of the Image
                            /*switched left and right*/
                             //  telemetry.addData("cXBlock",);
                            telemetry.addData("stoneCX", stoneCX);


                            if(stoneCX < screenCX){
                                /*changed math to use cXBlock instead of addition*/
                                /* tried using negative values and switched the thing around a bit*/
                                front_left = 1;
                                rear_left = 1;
                                front_right = 0;
                                rear_right = 0;
                                telemetry.addData("Action", "Go left");
                            } else if(stoneCX > screenCX){
                                front_left = -1;
                                rear_left= -1;
                                front_right = 0;
                                rear_right = 0;
                                telemetry.addData("Action", "Go right");
                            } else {
                                front_left = 0;
                                rear_left = 0;
                                front_right = 0;
                                rear_right = 0;
                                telemetry.addData("Action", "Your good");
                            }




                            frontLeftWheel.setPower(front_left * flwChange);
                            backLeftWheel.setPower(front_right * frwChange);
                            frontRightWheel.setPower(rear_left * blwChange);
                            backRightWheel.setPower(-rear_right * brwChange);

                            telemetry.addData("rear left", frontRightWheel.getPower());
                            telemetry.addData("front left", frontLeftWheel.getPower());
                            telemetry.addData("rear right", backRightWheel.getPower());
                            telemetry.addData("front right", backLeftWheel.getPower());
                        }
                        telemetry.update();
                    }
                }
            }
        }

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}
