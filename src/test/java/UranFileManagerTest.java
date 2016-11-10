import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

import javafx.embed.swing.JFXPanel;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UranFileManagerTest {

  @BeforeClass
  public static void initToolkit() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    SwingUtilities.invokeLater(() -> {
      new JFXPanel(); // initializes JavaFX environment
      latch.countDown();
    });

    if (!latch.await(5L, TimeUnit.SECONDS))
      throw new ExceptionInInitializerError();
  }

  @Test
  public void doubleToStringWithAccuracy() throws Exception {
    //given
    double d = 12.34567890;
    int accuracy2Digit = 2;
    int accuracy3Digit = 3;
    String expectedString2Digit = "12.34";
    String expectedString3Digit = "12.345";

    UranFileManager uranFileManager = new UranFileManager();

    //when
    String actualString2Digit = uranFileManager.doubleToStringWithAccuracy(d, accuracy2Digit);
    String actualString3Digit = uranFileManager.doubleToStringWithAccuracy(d, accuracy3Digit);

    //then

    assertThat(actualString2Digit, is(expectedString2Digit));
    assertThat(actualString3Digit, is(expectedString3Digit));
  }

  @Test
  public void parseElementToFilenameWithoutSize() throws Exception {
    //given
    String inputFolderWithoutSize = "D lib 2016.09.28 10:32";
    String expectedElementWithoutSize = "lib";
    UranFileManager uranFileManager = new UranFileManager();
    uranFileManager.toggleButton.setSelected(false);

    //when
    String actualElementWithoutSize = uranFileManager.parseElementToFilename(inputFolderWithoutSize);

    //then
    assertThat(actualElementWithoutSize, is(expectedElementWithoutSize));
  }

  @Test
  public void parseElementToFilenameWithSize() throws Exception {
    //given
    String inputFolderWithSize = "D IntelliJ IDEA 2016.2.4 991.56Mbyte 2016.09.28 10:31";
    String expectedElementWithSize = "IntelliJ IDEA 2016.2.4";
    UranFileManager uranFileManager = new UranFileManager();
    uranFileManager.toggleButton.setSelected(true);

    //when
    String actualElementWithSize = uranFileManager.parseElementToFilename(inputFolderWithSize);

    //then
    assertThat(actualElementWithSize, is(expectedElementWithSize));
  }

  @Test
  public void printSizeFile() throws Exception {
    //given
    UranFileManager uranFileManager = new UranFileManager();
    long sizeByte  = 123;
    long sizeKByte = 123456;
    long sizeMByte = 12345678;
    long sizeGByte = 1234567890;
    String expectedElementSizeByte = "123Byte";
    String expectedElementSizeKByte = "120.56KByte";
    String expectedElementSizeMByte = "11.77MByte";
    String expectedElementSizeGByte = "1.14GByte";

    //when
    String actualElementSizeByte = uranFileManager.printSizeFile(sizeByte);
    String actualElementSizeKByte = uranFileManager.printSizeFile(sizeKByte);
    String actualElementSizeMByte = uranFileManager.printSizeFile(sizeMByte);
    String actualElementSizeGByte = uranFileManager.printSizeFile(sizeGByte);

    //then
    assertThat(actualElementSizeByte, is(expectedElementSizeByte));
    assertThat(actualElementSizeKByte, is(expectedElementSizeKByte));
    assertThat(actualElementSizeMByte, is(expectedElementSizeMByte));
    assertThat(actualElementSizeGByte, is(expectedElementSizeGByte));

  }


}