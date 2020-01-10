package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcontroller.external.samples.HardwarePushbot;

/**
 * This file illustrates the concept of driving a path based on encoder counts.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 * <p>
 * The code REQUIRES that you DO have encoders on the wheels,
 * otherwise you would use: PushbotAutoDriveByTime;
 * <p>
 * This code ALSO requires that the drive Motors have been configured such that a positive
 * power command moves them forwards, and causes the encoders to count UP.
 * <p>
 * The desired path in this example is:
 * - Drive forward for 48 inches
 * - Spin right for 12 Inches
 * - Drive Backwards for 24 inches
 * - Stop and close the claw.
 * <p>
 * The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeoutS)
 * that performs the actual movement.
 * This methods assumes that each movement is relative to the last stopping place.
 * There are other ways to perform encoder based moves, but this method is probably the simplest.
 * This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name = "Pushbot: Auto Drive By Encoder", group = "Pushbot")
public class Auto extends LinearOpMode {

    //names of the motors and servos
    private DcMotor frontLeftWheel;
    private DcMotor backLeftWheel;
    private DcMotor frontRightWheel;
    private DcMotor backRightWheel;

    private DcMotor armHorizontal;
    private DcMotor armVertical;
    private Servo armGrab;
    private Servo servoRight;
    private Servo servoLeft;

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_MOTOR_REV = 1680;    // eg: TETRIX Motor Encoder
    static final double DRIVE_GEAR_REDUCTION = 2.0;     // This is < 1.0 if geared UP
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double DRIVE_SPEED = 0.6;
    static final double TURN_SPEED = 0.5;


    @Override
    public void runOpMode() {
        //mapping motors
        frontLeftWheel = hardwareMap.dcMotor.get("frontLeft");
        backRightWheel = hardwareMap.dcMotor.get("backRight");
        frontRightWheel = hardwareMap.dcMotor.get("frontRight");
        backLeftWheel = hardwareMap.dcMotor.get("backLeft");
        armHorizontal = hardwareMap.dcMotor.get("armHorizontal");
        armVertical = hardwareMap.dcMotor.get("armVertical");
        armGrab = hardwareMap.servo.get("armGrab");
        servoRight = hardwareMap.servo.get("servoRight");
        servoLeft = hardwareMap.servo.get("servoLeft");

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */

        //makes sure arm is in up against the arm, so the arm can be pushed back
        armGrab.setPosition(.8);

        //moves the servos outward
        servoLeft.setPosition(0);
        servoRight.setPosition((1));

        //shows that servos have been set
        telemetry.addData("Status", "Resetting Encoders");//
        telemetry.update();

        //setting and running the motors, will user encoders eventually
        armHorizontal.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armVertical.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        armHorizontal.setTargetPosition(0);
        armVertical.setTargetPosition(0);

        armHorizontal.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armVertical.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Send telemetry message to indicate successful Encoder reset

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        //if robot is lined up behind the center of base, it will move forward grab and drag base into builing zone
        //then it will strafe sideways under the center
        encoderDrive(DRIVE_SPEED, 6, 0, 2, 1);  // S1: Forward 47 Inches with 5 Sec timeout
        servoRight.setPosition(.5);
        servoLeft.setPosition(.5);
        sleep(200);
        encoderDrive(TURN_SPEED, -14, 0, 2, -1);  // S2: Turn Right 12 Inches with 4 Sec timeout
        servoRight.setPosition(1);
        servoLeft.setPosition(0);
        sleep(200);
        armVertical.setPower(-.2);
        sleep(2000);
        armVertical.setPower(0);
        encoderDrive(DRIVE_SPEED, 0, 14, 2, 1);  // S3: Reverse 24 Inches with 4 Sec timeout

        sleep(1000);     // pause for servos to move

        telemetry.addData("Path", "Complete");
        telemetry.update();
    }

    /*
     *  Method to perfmorm a relative move, based on encoder counts.
     *  Encoders are not reset as the move is based on the current position.
     *  Move will stop if any of three conditions occur:
     *  1) Move gets to the desired position
     *  2) Move runs out of time
     *  3) Driver stops the opmode running.
     */
    public void encoderDrive(double speed, double forwardSeconds, double rightSeconds, double timeoutS, double negative) {

        //moves forward/right the for 1/5 the inputed time
        frontLeftWheel.setPower(-speed * negative);
        frontRightWheel.setPower(speed * negative);
        backLeftWheel.setPower(speed * negative);
        backRightWheel.setPower(-speed * negative);
        sleep((long) (Math.abs(200 * forwardSeconds)));

        frontLeftWheel.setPower(-speed * negative);
        frontRightWheel.setPower(-speed * negative);
        backLeftWheel.setPower(speed * negative);
        backRightWheel.setPower(speed * negative);
        sleep(Math.abs((long) (200 * rightSeconds)));

        telemetry.update();
        reset(timeoutS);
    }

    public void reset(double timeout) {
        frontLeftWheel.setPower(0);
        frontRightWheel.setPower(0);
        backLeftWheel.setPower(0);
        backRightWheel.setPower(0);
        sleep((long) (1000 * timeout));
    }
}
