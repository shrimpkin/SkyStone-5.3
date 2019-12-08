/* Copyright (c) 2017 FIRST. All rights reserved.
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

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * This file contains an example of an iterative (Non-Linear) "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all iterative OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Wireless Drive Controls", group="Iterative Opmode")
//@Disabled
public class DriveControls extends OpMode
{
    //names of the motors
    private DcMotor frontLeftWheel;
    private DcMotor backLeftWheel;
    private DcMotor frontRightWheel;
    private DcMotor backRightWheel;

    //timers and fields allowing for slow mode to occur
    private long StoredTimeForSlow;
    private boolean isFirstTime = true;
    private boolean isSlow = false;

    //constants for adjusting robot moving based off of weight distribution and are also adjusted for slow mode by 1/4
    private double flwChange = 1;
    private double blwChange = 1;
    private double frwChange = 1;
    private double brwChange = 1;

    //timers for updating motor accleration
    private long StoredTimeForMotors;

    //actual motor power assignments
    private float front_left;
    private float rear_left;
    private float front_right;
    private float rear_right;

    //target motor power assignments
    private float targetF_L;
    private float targetR_L;
    private float targetF_R;
    private float targetR_R;

    //movements values gained from the remote controller
    private float clockwise;
    private float right;
    private float forward;
    private float rTrigger;
    private float lTrigger;

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        //maps each dcMotor to the corresponding name listed on the "Configure Robot"
        frontLeftWheel = hardwareMap.dcMotor.get("frontLeft");
        backRightWheel = hardwareMap.dcMotor.get("backRight");
        frontRightWheel = hardwareMap.dcMotor.get("frontRight");
        backLeftWheel =  hardwareMap.dcMotor.get("backLeft");

        //telemetry sends data to robot controller
        telemetry.addData("Output", "hardwareMapped");

    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        //getting vertical position of left joystick
        clockwise = gamepad1.right_stick_y;

        //getting horiz position of right joystick
        right = -gamepad1.left_stick_x;

        //
        lTrigger = gamepad1.left_trigger;

        //
        rTrigger = gamepad1.right_trigger;

        if(lTrigger > 0) {
            forward = -lTrigger;
        } else {
            forward = rTrigger;
        }

        boolean isB = gamepad1.b;

        //Doing the math to find the power value we need to set
        float temp = (float) (forward* (Math.cos(clockwise)) + right* (Math.sin(clockwise)));
        right = (float) ( -forward* (Math.sin(clockwise)) + right*(Math.cos(clockwise)));
        forward = temp;

        front_left = forward + clockwise + right;
        front_right = forward - clockwise - right;
        rear_left = forward + clockwise - right;
        rear_right = forward - clockwise + right;

        //targetF_L = forward + clockwise + right;
        //targetF_R = forward - clockwise - right;
        //targetR_L = forward + clockwise - right;
        //targetR_R = forward - clockwise + right;

        //makes sure values found are not outside of range of possible inputs   x: (-1, 1)
        front_left = clip(front_left,-1,1);
        front_right = clip(front_right, -1, 1);
        rear_right = clip(rear_right, -1, 1);
        rear_left = clip(rear_left, -1,1);

        //processing whether a slow mode should be implemented
        if(isB && (StoredTimeForSlow + 1000  > System.currentTimeMillis() || isFirstTime)) {
            StoredTimeForSlow = System.currentTimeMillis();
            isSlow = !isSlow;
            isFirstTime = false;
        }

        //if yes slows robot by 1/4
        if(isSlow) {
            flwChange = .5;
            frwChange = .5;
            brwChange = .5;
            blwChange = .5;
        }



        //slow acceleration, check method for details
        //front_left = accelerate(targetF_L, front_left);
        //front_right=  accelerate(targetF_R, front_right);
        //rear_left = accelerate(targetR_L, rear_left);
        //rear_right = accelerate(targetR_R, rear_right);

        //sends power value to the wheels
        frontLeftWheel.setPower(-front_left * flwChange);
        backLeftWheel.setPower(-front_right * frwChange);
        frontRightWheel.setPower(-rear_left * blwChange);
        backRightWheel.setPower(rear_right * brwChange);

        //reports data to controller
        telemetry.addData("rear left", frontRightWheel.getPower());
        telemetry.addData("front left", frontLeftWheel.getPower());
        telemetry.addData("rear right", backRightWheel.getPower());
        telemetry.addData("front right", backLeftWheel.getPower());
        telemetry.addData("forward", forward);
        telemetry.addData("right", right);
        telemetry.addData("clockwise", clockwise);
        telemetry.addData("isSlow", isSlow);

    }

    private float clip(float originalNumber, float min, float max)  {
        //takes a number and makes sure it is inbetween min and max, if is bigger/smaller returns max/min respectively
        if(originalNumber < min){
            return min;
        } else if (originalNumber > max) {
            return max;
        }
        return originalNumber;
    }

    private float accelerate(float targetValue, float value) {
        //makes sure 200 milliseconds has passed since the last update
        if(StoredTimeForMotors + 200 < System.currentTimeMillis()) {
            StoredTimeForMotors = System.currentTimeMillis();

            //calculates how value should change to get closer to target value
            if(Math.abs(targetValue - value) < .1) {
                value = targetValue;
            } else {
                if(targetValue > value) {
                    value = targetValue - (float).1;
                } else {
                    value = targetValue + (float).1;
                }
            }

        }
        return value;

    }

}
