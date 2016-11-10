import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class UranFileManager {

  final Button rootButton = new Button("ROOT");
  final Button upFolderButton = new Button("UpFolder");
  final TextField currentFolderTextField = new TextField();
  ObservableList<String> listFileAndFolders = FXCollections.observableArrayList();
  ListView<String> listView = new ListView<>(listFileAndFolders);
  ToolBar addressButtonToolbar = new ToolBar(rootButton);
  ToggleButton toggleButton = new ToggleButton("Show size of folders");

  private String currentFolder;

  public String getCurrentFolder() {
    return currentFolder;
  }

  public void setCurrentFolder(String currentFolder) {
    this.currentFolder = currentFolder;
  }

  public UranFileManager() throws UnknownHostException {
    listView.setMinSize(590, 505);
    gotoRoot();

    listView.setOnMouseClicked((e) -> {
      navigate(listView.getSelectionModel().getSelectedItem());
    });

    rootButton.setOnMouseReleased((e) -> {
      gotoRoot();
    });

    upFolderButton.setOnMouseReleased((e) -> {
      upFolder(getCurrentFolder());
    });

    currentFolderTextField.setOnKeyPressed((key) -> {
      if (key.getCode() == KeyCode.ENTER) {
        if (new File(currentFolderTextField.getText()).exists()) {
          gotoFolder(currentFolderTextField.getText());
        }
      }
    });
  }

  public Pane configurePane() {
    toggleButton.setTooltip(new Tooltip("on/off calculate size folder while opening"));
    upFolderButton.setTooltip(new Tooltip("go to parent folder"));
    rootButton.setTooltip(new Tooltip("go to ROOT folder"));
    BorderPane bp = new BorderPane();
    bp.setPadding(new Insets(5, 5, 5, 5));

    HBox top = new HBox(new Group());
    top.getChildren().add(upFolderButton);
    top.getChildren().add(new Separator());
    top.getChildren().add(toggleButton);
    bp.setTop(top);

    Button[] buttonArray = createButtonsAvailableDisk(checkAvailableDisk());
    ToolBar toolbarButtonsDisk = new ToolBar(buttonArray);

    VBox center = new VBox();
    center.getChildren().addAll(toolbarButtonsDisk);
    center.getChildren().add(currentFolderTextField);
    center.getChildren().addAll(addressButtonToolbar);
    center.getChildren().add(listView);
    bp.setCenter(center);

    return bp;
  }

  public String[] getListFilesAndSubfolders(String folder) {
    String[] listFilesAndSubfolders = new File(folder).list();
    return listFilesAndSubfolders;
  }

  public void upFolder(String folder) {
    if (new File(folder).getParent() == null) {
      gotoRoot();
    } else {
      gotoFolder(new File(folder).getParent());
    }
//    gotoFolder(folder.substring(0, folder.substring(0, folder.length() - 1).lastIndexOf('\\')));
  }

  public void parsePathToButtons() {
    String[] folders = getCurrentFolder().split("\\\\");
    int count = getCurrentFolder().equals("") ? 0 : folders.length;
    Button[] buttons = new Button[count + 1];
    buttons[0] = rootButton;
    for (int i = 1; i < count + 1; i++) {
      buttons[i] = new Button(folders[i - 1]);
      if (i > 1) {
        buttons[i].setId(buttons[i - 1].getId() + "\\" + buttons[i].getText());
      }
      if (i == 1) {
        buttons[i].setId(buttons[i].getText());
      }
      int j = i;
      buttons[j].setOnMouseReleased((e) -> {
        gotoFolder(buttons[j].getId());
      });
    }

    ObservableList<Node> observableList = addressButtonToolbar.getItems();
    observableList.clear();
    observableList.addAll(buttons);
  }

  public void gotoRoot() {
    upFolderButton.setDisable(true);
    currentFolderTextField.setText("MyComputer");
    setCurrentFolder("");
    showContents(checkAvailableDisk());
    parsePathToButtons();
    //    showContents(new String[]{"Y:\\"});
  }

  public void gotoFolder(String directoryPath) {
    boolean isUpButtonDisable = directoryPath.length() > 0 ? false : true;
    upFolderButton.setDisable(isUpButtonDisable);

    if ((directoryPath.lastIndexOf('\\') + 1) != directoryPath.length()) {
      directoryPath = directoryPath + "\\";
    }
    setCurrentFolder(directoryPath);
    currentFolderTextField.setText(getCurrentFolder());
    showContents(getListFilesAndSubfolders(getCurrentFolder()));
    parsePathToButtons();
  }

  public void navigate(String selectedElement) {
    if (selectedElement != null) {
      String element = parseElementToFilename(selectedElement);
      File file = new File(getCurrentFolder() + element);
      if (file.isDirectory()) {
        if (getCurrentFolder().equals("")) {
          gotoFolder(element);
        } else {
          gotoFolder(getCurrentFolder() + element);
        }
      }
    }
  }

  public String parseElementToFilename(String selectedElement) {
    String element;
    if (toggleButton.isSelected()) {
      element = selectedElement.substring(2, selectedElement.lastIndexOf(' '));
      element = element.substring(0, element.lastIndexOf(' '));
      element = element.substring(0, element.lastIndexOf(' '));
    } else {
      element = selectedElement.substring(2, selectedElement.lastIndexOf(' '));
      element = element.substring(0, element.lastIndexOf(' '));
    }
    return element;
  }

  public void showContents(String[] listFilesAndSubfolders) {
    if (listFilesAndSubfolders != null) {
      File file;
      for (int i = 0; i < listFilesAndSubfolders.length; i++) {
        file = new File(getCurrentFolder() + "\\" + listFilesAndSubfolders[i]);
        if (file.isDirectory()) {
          if (toggleButton.isSelected()) {
            listFilesAndSubfolders[i] = "D " + listFilesAndSubfolders[i] + " " + printSizeFile(calculateSizeFolder(file)) + " " + dateOfCreationFile(file);
          } else {
            listFilesAndSubfolders[i] = "D " + listFilesAndSubfolders[i] + " " + dateOfCreationFile(file);
          }
        } else {
          listFilesAndSubfolders[i] = "F " + listFilesAndSubfolders[i] + " " + printSizeFile(file.length()) + " " + dateOfCreationFile(file);
        }
      }
      listFileAndFolders.clear();
      Arrays.sort(listFilesAndSubfolders);
      listFileAndFolders.addAll(listFilesAndSubfolders);
    }
  }

  public long calculateSizeFolder(final File file) {
    long size = 0;
    File[] listFile = file.listFiles();
    for (int i = 0; i < listFile.length; i++) {
      if (listFile[i].isFile()) {
        size += listFile[i].length();
      } else {
        size += calculateSizeFolder(listFile[i]);
      }
    }
    return size;
  }

  public String dateOfCreationFile(final File file) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
      BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      return sdf.format(attr.creationTime().toMillis());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "date n/a";
  }

  public String printSizeFile(final long size) {
    final int ACCURACY = 2;
    String sizeString = "";
    if (size < 1024) {
      sizeString = size + "Byte";
    }
    if (size >= 1024 && size < 1024 * 1024) {
      sizeString = doubleToStringWithAccuracy(size * 1.0 / 1024, ACCURACY) + "KByte";
    }
    if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
      sizeString = doubleToStringWithAccuracy(size * 1.0 / (1024 * 1024), ACCURACY) + "MByte";
    }
    if (size >= 1024 * 1024 * 1024) {
      sizeString = doubleToStringWithAccuracy(size * 1.0 / (1024 * 1024 * 1024), ACCURACY) + "GByte";
    }
    return sizeString;
  }

  public String doubleToStringWithAccuracy(final double d, final int accuracy) {
    String doubleString = String.valueOf(d + 0.000000001);
    int dotPosition = doubleString.indexOf('.');
    return doubleString.substring(0, dotPosition + accuracy + 1);
  }

  public Button[] createButtonsAvailableDisk(String[] arrayAvailableDisk) {
    Button[] buttonArray = new Button[arrayAvailableDisk.length];
    for (int i = 0; i < arrayAvailableDisk.length; i++) {
      Button button = new Button(arrayAvailableDisk[i]);
      button.setOnAction(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          gotoFolder(button.getText());
        }
      });
      buttonArray[i] = button;
    }
    return buttonArray;
  }

  public String[] checkAvailableDisk() {
    File[] fileArray = File.listRoots();
    String[] arrayAvailableDisk = new String[fileArray.length];
    for (int i = 0; i < fileArray.length; i++) {
      arrayAvailableDisk[i] = fileArray[i].toString();
    }
    return arrayAvailableDisk;
  }
}