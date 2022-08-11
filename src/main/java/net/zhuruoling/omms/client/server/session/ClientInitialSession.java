package net.zhuruoling.omms.client.server.session;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import net.zhuruoling.omms.client.ConsoleLogger;
import net.zhuruoling.omms.client.command.Command;
import net.zhuruoling.omms.client.message.Message;
import net.zhuruoling.omms.client.util.ConnectionFailException;
import net.zhuruoling.omms.client.util.EncryptedConnector;
import net.zhuruoling.omms.client.util.Util;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Objects;

public class ClientInitialSession {
    InetAddress inetAddress;
    int port;
    public ClientInitialSession(InetAddress inetAddress, int port){
        this.port = port;
        this.inetAddress = inetAddress;
    }

    public ClientSession init(int code) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ConnectionFailException, InterruptedException {
        Socket socket = new Socket(this.inetAddress,this.port);
        socket.setKeepAlive(true);

        LocalDateTime date = LocalDateTime.now();
        String key = date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"));
        key = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
        key = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
        EncryptedConnector connector = new EncryptedConnector(
                new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                ),
                new PrintWriter(new OutputStreamWriter(socket.getOutputStream())),
                key
        );
        long timeCode = Long.parseLong(date.format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")));
        String connCode = String.valueOf(timeCode ^ code);
        connCode = Util.base64Encode(connCode);
        connCode = Util.base64Encode(connCode);
        Gson gson = new GsonBuilder().serializeNulls().create();
        String content = gson.toJson(new Command("PING", new String[]{connCode}));
        connector.send(content);
        String line = connector.readLine();
        Message message = gson.fromJson(line, Message.class);
        if (Objects.equals(message.getMsg(), "OK")){
            String newKey = message.getLoad()[0];
            EncryptedConnector newConnector = new EncryptedConnector(
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream())
                    ),
                    new PrintWriter(new OutputStreamWriter(socket.getOutputStream())),
                    newKey
            );
            return new ClientSession(newConnector, socket);
        }
        else {
            throw new ConnectionFailException(String.format("Server returned ERR_CODE:%s", message.getMsg()));
        }

    }
}
