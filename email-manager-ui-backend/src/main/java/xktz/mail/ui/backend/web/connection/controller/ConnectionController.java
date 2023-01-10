package xktz.mail.ui.backend.web.connection.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xktz.mail.ui.backend.result.Result;
import xktz.mail.ui.backend.web.connection.service.ConnectionService;

/**
 * Controller for connections
 *
 * @author XKTZ
 * @date 2022-06-29
 */
@RestController
@Slf4j
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> connect(@RequestBody LoginInformation loginInfo) {
        log.info(loginInfo.toString());
        connectionService.connect(loginInfo.imapServer, loginInfo.imapPort, loginInfo.smtpServer, loginInfo.smtpPort,
                loginInfo.username, loginInfo.password);
        return Result.EMPTY_SUCCESS_RESULT;
    }

    @RequestMapping(value = "/logined", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> connected() {
        return Result.successResultOf(connectionService.isConnected());
    }

    public static class LoginInformation {
        @JsonProperty("imapServer")
        public String imapServer;

        @JsonProperty("imapPort")
        public int imapPort;

        @JsonProperty("smtpServer")
        public String smtpServer;

        @JsonProperty("smtpPort")
        public int smtpPort;

        @JsonProperty("username")
        public String username;

        @JsonProperty("password")
        public String password;

        @Override
        public String toString() {
            return "LoginInformation{" +
                    "imapServer='" + imapServer + '\'' +
                    ", imapPort=" + imapPort +
                    ", smtpServer='" + smtpServer + '\'' +
                    ", smtpPort=" + smtpPort +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
