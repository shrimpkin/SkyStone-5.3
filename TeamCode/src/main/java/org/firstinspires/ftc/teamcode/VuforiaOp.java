package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@TeleOp(name="Vuforia Testing", group="Iterative Opmode")

public class VuforiaOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "Abyd5Br/////AAABmRdvKE0dIk+VoE3dZUX80Qgvs0taq2LkrHwvZqfHxuRb/7H5kBamN+WGMyUKT/ECCRxHZ9umWluLjUXas7DxXSDi3BWN1FbACcFP9JllAwNAYwv98E0LTkAfd6Wqmg/xLsYUf2horiU5L+yPqaiho9MU/Kuan1Rv9YKZXgFHRneI84YbjsUuhLzM+yO63k1rQqFtUlSGZA64WI/lH/+simvahLPT+XbSeU8pCff3RB26LfRJUqscB3PE43E9+gdBqZ8S5wYChNKO3YDBf6wpGXhp7+Qj13X+7TC+CPykzyTHSShS1vGw8QMwbvZN3/WNLgkxV/o2bA3dbXCTrdXFNFO0wURB7o4YRxhTrj9q/2Jf ";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS,6);

        VuforiaTrackables TargetElements = vuforia.loadTrackablesFromAsset("Skystone");
        VuforiaTrackable stoneTarget = TargetElements.get(0);
        stoneTarget.setName("TargetElement");
        TargetElements.activate();

        waitForStart();

        TargetElements.activate();

        while (opModeIsActive()) {
            for(VuforiaTrackable object : TargetElements) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) object.getListener()).getPose();

                if(pose != null) {
                    VectorF translation = pose.getTranslation();

                    telemetry.addData(object.getName() + "-Translation", translation);

                    double degreesToTurn = Math.toDegrees(Math.atan2(translation.get(1), translation.get(2)));
                    telemetry.addData(object.getName() + "-Degrees", degreesToTurn);
                }
            }
            telemetry.update();
        }
    }

}

