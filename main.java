import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class Camera {

    private static final int WIDTH = 640;

    private static final int HEIGHT = 480;

    private ExecutorService executorService;

    private BufferedImage image;

    public Camera() {

        executorService = Executors.newSingleThreadExecutor();

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    }

    public void start() {

        executorService.submit(new Runnable() {

            @Override

            public void run() {

                while (true) {

                    try {

                        takePicture();

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }

            }

        });

    }

    public void stop() {

        executorService.shutdownNow();

    }

    public BufferedImage getImage() {

        return image;

    }

    private void takePicture() throws IOException {

        // Get the current frame from the camera

        byte[] data = getFrame();

        // Decode the frame into a BufferedImage

        image = ImageIO.read(new ByteArrayInputStream(data));

    }

    private byte[] getFrame() throws IOException {

        // Open a connection to the camera

        java.net.Socket socket = new java.net.Socket("192.168.1.100", 8080);

        // Send a request to the camera to take a picture

        String request = "GET / HTTP/1.1\r\n" +

                "Host: 192.168.1.100:8080\r\n" +

                "Connection: close\r\n" +

                "\r\n";

        socket.getOutputStream().write(request.getBytes());

        // Read the response from the camera

        byte[] response = new byte[1024];

        int bytesRead = socket.getInputStream().read(response);

        // Close the connection

        socket.close();

        // Return the response

        return Arrays.copyOf(response, bytesRead);

    }

    public static void main(String[] args) throws IOException {

        Camera camera = new Camera();

        camera.start();

        // Save the image to a file

        ImageIO.write(camera.getImage(), "jpg", new File("image.jpg"));

        // Display the image on the screen

        javax.swing.JFrame frame = new javax.swing.JFrame();

        javax.swing.JLabel label = new javax.swing.JLabel(new javax.swing.ImageIcon(camera.getImage()));

        frame.getContentPane().add(label);

        frame.pack();

        frame.setVisible(true);

    }

}

