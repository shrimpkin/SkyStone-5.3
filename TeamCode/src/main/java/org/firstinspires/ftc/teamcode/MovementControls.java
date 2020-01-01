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
import com.qualcomm.robotcore.hardware.Servo;


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

@TeleOp(name="Movement Controls", group="Iterative Opmode")
//@Disabled
public class  MovementControls extends OpMode
{
    //names of the motors
    private DcMotor frontLeftWheel;
    private DcMotor backLeftWheel;
    private DcMotor frontRightWheel;
    private DcMotor backRightWheel;

    private DcMotor armHorizontal;
    private DcMotor armVertical;
    private Servo armGrab;

    //timers and fields allowing for slow mode to occur
    private boolean isSlow = false;

    //constants for adjusting robot moving based off of weight distribution and are also adjusted for slow mode by 1/4
    private double flwChange = 1;
    private double blwChange = 1;
    private double frwChange = 1;
    private double brwChange = 1;
    private double armHorizChange = 1;
    private double armVertChange = 1;

    //actual motor power assignments
    private float front_left;
    private float rear_left;
    private float front_right;
    private float rear_right;
    private float arm_horiz;
    private float arm_vert;

    //movements values gained from the remote controller
    private float clockwise;
    private float right;
    private float forward;
    private float forward2;
    private float rTrigger;
    private float lTrigger;
    private boolean lButton;
    private boolean rButton;

    //used for deciding which motors to use
    private String movementMode;

    private String log = "absolutely nothing";
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
        armHorizontal = hardwareMap.dcMotor.get("armHorizontal");
        armVertical = hardwareMap.dcMotor.get("armVertical");
        armGrab = hardwareMap.servo.get("armGrab");


        //telemetry sends data to robot controller
        telemetry.addData("Output", "hardwareMapped");
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        float servoMovement = gamepad2.left_stick_y;
        armGrab.setPosition(servoMovement);

        //getting vertical position of left joystick
        forward = gamepad1.right_stick_y;
        forward2 = gamepad1.left_stick_y;

        if(Math.abs(forward2) > Math.abs(forward)) {
            forward = forward2;
        }

        lButton = gamepad1.left_bumper;
        rButton = gamepad1.right_bumper;

        if(lButton) {
            right = -1;
        } else if(rButton) {
            right = 1;
        } else {
            right = 0;
        }

        //
        lTrigger = gamepad1.left_trigger;

        //
        rTrigger = gamepad1.right_trigger;

        if(lTrigger > 0) {
            clockwise = -lTrigger;
        } else {
            clockwise = rTrigger;
        }

        if(Math.abs(forward) > Math.abs(right) && Math.abs(forward) > Math.abs(clockwise)) {
            movementMode = "forward";
        } else if(Math.abs(right) > Math.abs(clockwise)) {
            movementMode = "strafe";
        } else if(Math.abs(clockwise) > 0) {
            movementMode = "turn";
        } else {
            movementMode = "no value";
        }

        if(movementMode.equals("forward")) {
            //sends power value to the wheels
            frontLeftWheel.setPower(forward * flwChange);
            backLeftWheel.setPower(-forward * frwChange);
            frontRightWheel.setPower(-forward * blwChange);
            backRightWheel.setPower(forward * brwChange);

        } else if (movementMode.equals("strafe")) {
            frontLeftWheel.setPower(-right * flwChange);
            backLeftWheel.setPower(right * frwChange);
            frontRightWheel.setPower(-right * blwChange);
            backRightWheel.setPower(right * brwChange);

        } else if(movementMode.equals("turn")){
            frontLeftWheel.setPower(-clockwise * flwChange);
            backLeftWheel.setPower(-clockwise * frwChange);
            frontRightWheel.setPower(-clockwise * blwChange);
            backRightWheel.setPower(-clockwise * brwChange);

        } else {
            frontLeftWheel.setPower(0);
            backLeftWheel.setPower(0);
            frontRightWheel.setPower(0);
            backRightWheel.setPower(0);
        }
        arm_horiz = gamepad2.right_stick_x;
        arm_vert = gamepad2.right_stick_y;

        armHorizontal.setPower(arm_horiz * armHorizChange);
        armVertical.setPower(arm_vert * armVertChange);

        //makes sure values found are not outside of range of possible inputs   x: (-1, 1)
        front_left = clip(front_left,-1,1);
        front_right = clip(front_right, -1, 1);
        rear_right = clip(rear_right, -1, 1);
        rear_left = clip(rear_left, -1,1);

        boolean isB = gamepad1.b;

        //processing whether a slow mode should be implemented
        if(isB) {
            isSlow = true;
        } else {
            isSlow = false;
        }

        //if yes slows robot by 1/5
        if(isSlow) {
            flwChange = .2;
            frwChange = .2;
            brwChange = .2;
            blwChange = .2;
        } else {
            flwChange = 1;
            frwChange = 1;
            brwChange = 1;
            blwChange = 1;
        }
        //  reports data to controller
        telemetry.addData("arm grabber", armGrab.getPosition());
        telemetry.addData("arm horiz", armHorizontal.getPower());
        telemetry.addData("arm vert", armVertical.getPower());
        telemetry.addData("rear left", frontRightWheel.getPower());
        telemetry.addData("front left", frontLeftWheel.getPower());
        telemetry.addData("rear right", backRightWheel.getPower());
        telemetry.addData("front right", backLeftWheel.getPower());
        telemetry.addData("forward", forward);
        telemetry.addData("right", right);
        telemetry.addData("clockwise", clockwise);
        telemetry.addData("isSlow", isSlow);
        telemetry.addData("MovementMode", movementMode);
        telemetry.addData("acceleration", log);
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

}
