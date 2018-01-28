package com.nc.exception;

import com.nc.Constants;
import javafx.scene.control.Alert;

public class TaskManagerException {
    public static void createLoadingException(String filePath, Exception e)
    {
        createException(
                Constants.ERROR,
                Constants.FAILED_TO_LOAD_DATA,
                Constants.FAILED_TO_LOAD_DATA + Constants.FROM + filePath,
                e);
    }

    public static void createSavingException(String filePath, Exception e)
    {
        createException(
                Constants.ERROR,
                Constants.FAILED_TO_SAVE_DATA,
                Constants.FAILED_TO_SAVE_DATA + Constants.TO + filePath,
                e);
    }
    private static void createException(String title, String headerText, String contentText, Exception e)
    {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }
}
