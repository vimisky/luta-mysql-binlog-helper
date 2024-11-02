package io.github.vimisky.luta.mysql.binlog.helper.controller;

import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogChannel;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogFilter;
import io.github.vimisky.luta.mysql.binlog.helper.entity.LutaBinlogTask;
import io.github.vimisky.luta.mysql.binlog.helper.service.LutaBinlogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Controller
@RequestMapping("/console")
public class LutaBinlogViewController {
    private static final Logger logger = LoggerFactory.getLogger(LutaBinlogViewController.class);

    @Autowired
    private LutaBinlogService binlogService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(String msg){
        return "login";
    }
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttr){
        redirectAttr.addAttribute("msg", "已退出");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(required=true) String username,
                        @RequestParam(required=true) String password,
                        RedirectAttributes redirectAttr){

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        if (authenticationResponse.isAuthenticated()){

            return "redirect:/console/task/list";
        }else {
            redirectAttr.addAttribute("msg", "登录失败");

            return "redirect:/console/login";
        }
    }

    @RequestMapping(value = "/task/create", method = RequestMethod.GET)
    public String taskCreate(Model model){
        String[] timeZones = TimeZone.getAvailableIDs();
        model.addAttribute("availableTimezoneIDs", timeZones);

        return "taskCreate";
    }
    @RequestMapping(value = "/task/edit", method = RequestMethod.GET)
    public String taskEdit(String taskUUID, Model model){
        LutaBinlogTask lutaBinlogTask = binlogService.getBinlogTask(taskUUID);
        model.addAttribute("taskDetail", lutaBinlogTask);

        String[] timeZones = TimeZone.getAvailableIDs();
        model.addAttribute("availableTimezoneIDs", timeZones);
        return "taskEdit";
    }
    @RequestMapping(value = "/task/edit", method = RequestMethod.POST)
    public String taskEdit(@RequestParam(required = true) Long id,
                            @RequestParam(required = true) String taskUUID,
                            @RequestParam(required = true) Long binlogChannelId,
                            @RequestParam(required=true) String name,
                             String description,
                             @RequestParam(required=true) String srcDriverClassName,
                             @RequestParam(required=true) String srcHost,
                             @RequestParam(required=true) Integer srcPort,
                             @RequestParam(required=true) String srcUsername,
                             @RequestParam(required=true) String srcPassword,
                             @RequestParam(required=true) String srcBinlogFilename,
                             @RequestParam(required=true) Long srcBinlogNextPosition,
                             @RequestParam(required=true) String remoteMysqlTimezone,
                             @RequestParam(required=true) String dstHost,
                             @RequestParam(required=true) Integer dstPort,
                             @RequestParam(required=true) String dstVhost,
                             @RequestParam(required=true) String dstUsername,
                             @RequestParam(required=true) String dstPassword,
                             @RequestParam(required=true) String dstTopicName,
                             @RequestParam(required=true) String dstRoutingKey,
                             RedirectAttributes redirectAttr)
    {
        LutaBinlogChannel lutaBinlogChannel = new LutaBinlogChannel();
        lutaBinlogChannel.setName(name);
        lutaBinlogChannel.setDescription(description);
        lutaBinlogChannel.setSrcDriverClassName(srcDriverClassName);
        lutaBinlogChannel.setSrcHost(srcHost);
        lutaBinlogChannel.setSrcPort(srcPort);
        lutaBinlogChannel.setSrcUsername(srcUsername);
        lutaBinlogChannel.setSrcPassword(srcPassword);
        lutaBinlogChannel.setSrcBinlogFilename(srcBinlogFilename);
        lutaBinlogChannel.setSrcBinlogNextPosition(srcBinlogNextPosition);
        lutaBinlogChannel.setRemoteMysqlTimezone(remoteMysqlTimezone);
        lutaBinlogChannel.setDstHost(dstHost);
        lutaBinlogChannel.setDstPort(dstPort);
        lutaBinlogChannel.setDstVhost(dstVhost);
        lutaBinlogChannel.setDstUsername(dstUsername);
        lutaBinlogChannel.setDstPassword(dstPassword);
        lutaBinlogChannel.setDstTopicName(dstTopicName);
        lutaBinlogChannel.setDstRoutingKey(dstRoutingKey);

        binlogService.updateBinlogTaskConfig(taskUUID, lutaBinlogChannel);
        binlogService.updateBinlogTaskPosition(taskUUID, srcBinlogFilename, srcBinlogNextPosition);

        redirectAttr.addAttribute("taskUUID", taskUUID);

        return "redirect:/console/task/detail";
    }
    @RequestMapping(value = "/task/create", method = RequestMethod.POST)
    public String taskCreate(@RequestParam(required=true) String name,
                             String description,
                             @RequestParam(required=true) String srcDriverClassName,
                             @RequestParam(required=true) String srcHost,
                             @RequestParam(required=true) Integer srcPort,
                             @RequestParam(required=true) String srcUsername,
                             @RequestParam(required=true) String srcPassword,
                             @RequestParam(required=true) String srcBinlogFilename,
                             @RequestParam(required=true) Long srcBinlogNextPosition,
                             @RequestParam(required=true) String remoteMysqlTimezone,
                             @RequestParam(required=true) String dstHost,
                             @RequestParam(required=true) Integer dstPort,
                             @RequestParam(required=true) String dstVhost,
                             @RequestParam(required=true) String dstUsername,
                             @RequestParam(required=true) String dstPassword,
                             @RequestParam(required=true) String dstTopicName,
                             @RequestParam(required=true) String dstRoutingKey,
                             RedirectAttributes redirectAttr)
    {
        LutaBinlogChannel lutaBinlogChannel = new LutaBinlogChannel();
        lutaBinlogChannel.setName(name);
        lutaBinlogChannel.setDescription(description);
        lutaBinlogChannel.setSrcDriverClassName(srcDriverClassName);
        lutaBinlogChannel.setSrcHost(srcHost);
        lutaBinlogChannel.setSrcPort(srcPort);
        lutaBinlogChannel.setSrcUsername(srcUsername);
        lutaBinlogChannel.setSrcPassword(srcPassword);
        lutaBinlogChannel.setSrcBinlogFilename(srcBinlogFilename);
        lutaBinlogChannel.setSrcBinlogNextPosition(srcBinlogNextPosition);
        lutaBinlogChannel.setRemoteMysqlTimezone(remoteMysqlTimezone);
        lutaBinlogChannel.setDstHost(dstHost);
        lutaBinlogChannel.setDstPort(dstPort);
        lutaBinlogChannel.setDstVhost(dstVhost);
        lutaBinlogChannel.setDstUsername(dstUsername);
        lutaBinlogChannel.setDstPassword(dstPassword);
        lutaBinlogChannel.setDstTopicName(dstTopicName);
        lutaBinlogChannel.setDstRoutingKey(dstRoutingKey);

        LutaBinlogTask lutaBinlogTask = binlogService.createBinlogTask(lutaBinlogChannel);

        redirectAttr.addAttribute("taskUUID", lutaBinlogTask.getUuid());

        return "redirect:/console/task/detail";
    }

    @RequestMapping("/task/detail")
    public String taskDetail(String taskUUID, Model model){
        LutaBinlogTask lutaBinlogTask = binlogService.getBinlogTask(taskUUID);
        model.addAttribute("taskDetail", lutaBinlogTask);
        return "taskDetail";
    }
    @RequestMapping("/task/list")
    public String taskList(Model model){
        return "taskList";
    }
    @RequestMapping("/filter/list")
    public String filterList(@RequestParam(required=true) Long binlogChannelId, Model model){
        model.addAttribute("binlogChannelId", binlogChannelId);
        return "filterList";
    }
    @RequestMapping("/filter/create")
    public String filterCreate(@RequestParam(required=true) Long binlogChannelId, Model model){
        model.addAttribute("binlogChannelId", binlogChannelId);
        return "filterCreate";
    }
    @RequestMapping(value = "/filter/create", method = RequestMethod.POST)
    public String filterCreate(
            @RequestParam(required=true) Long binlogChannelId,
            @RequestParam(required=true) String name,
            String description,
            @RequestParam(required=true) boolean schemaNameSpecified,
            @RequestParam(required=true) String schemaName,
            @RequestParam(required=true) boolean tableNameSpecified,
            @RequestParam(required=true) String tableName,
            @RequestParam(required=true) boolean insertIncluded,
            @RequestParam(required=true) boolean updateIncluded,
            @RequestParam(required=true) boolean deleteIncluded,
            @RequestParam(required=true) boolean ddlUpdate,
            RedirectAttributes redirectAttr
    ){
        LutaBinlogFilter lutaBinlogFilter = new LutaBinlogFilter();
        lutaBinlogFilter.setBinlogChannelId(binlogChannelId);
        lutaBinlogFilter.setName(name);
        lutaBinlogFilter.setDescription(description);
        lutaBinlogFilter.setSchemaNameSpecified(schemaNameSpecified);
        lutaBinlogFilter.setSchemaName(schemaName);
        lutaBinlogFilter.setTableNameSpecified(tableNameSpecified);
        lutaBinlogFilter.setTableName(tableName);
        lutaBinlogFilter.setInsertIncluded(insertIncluded);
        lutaBinlogFilter.setUpdateIncluded(updateIncluded);
        lutaBinlogFilter.setDeleteIncluded(deleteIncluded);
        lutaBinlogFilter.setDdlUpdate(ddlUpdate);

        binlogService.createBinlogFilter(lutaBinlogFilter);

        redirectAttr.addAttribute("id", lutaBinlogFilter.getId());

        return "redirect:/console/filter/detail";
    }
    @RequestMapping("/filter/edit")
    public String filterEdit(@RequestParam(required=true) Long id, Model model){
        LutaBinlogFilter lutaBinlogFilter = binlogService.getBinlogFilter(id);
        model.addAttribute("binlogFilter", lutaBinlogFilter);
        return "filterEdit";
    }
    @RequestMapping(value = "/filter/edit", method = RequestMethod.POST)
    public String filterEdit(
            @RequestParam(required=true) Long id,
            @RequestParam(required=true) Long binlogChannelId,
            @RequestParam(required=true) String name,
            String description,
            @RequestParam(required=true) boolean schemaNameSpecified,
            @RequestParam(required=true) String schemaName,
            @RequestParam(required=true) boolean tableNameSpecified,
            @RequestParam(required=true) String tableName,
            @RequestParam(required=true) boolean insertIncluded,
            @RequestParam(required=true) boolean updateIncluded,
            @RequestParam(required=true) boolean deleteIncluded,
            @RequestParam(required=true) boolean ddlUpdate,
            RedirectAttributes redirectAttr
    ){
        LutaBinlogFilter lutaBinlogFilter = new LutaBinlogFilter();
        lutaBinlogFilter.setId(id);
        lutaBinlogFilter.setBinlogChannelId(binlogChannelId);
        lutaBinlogFilter.setName(name);
        lutaBinlogFilter.setDescription(description);
        lutaBinlogFilter.setSchemaNameSpecified(schemaNameSpecified);
        lutaBinlogFilter.setSchemaName(schemaName);
        lutaBinlogFilter.setTableNameSpecified(tableNameSpecified);
        lutaBinlogFilter.setTableName(tableName);
        lutaBinlogFilter.setInsertIncluded(insertIncluded);
        lutaBinlogFilter.setUpdateIncluded(updateIncluded);
        lutaBinlogFilter.setDeleteIncluded(deleteIncluded);
        lutaBinlogFilter.setDdlUpdate(ddlUpdate);

        binlogService.updateBinlogFilter(lutaBinlogFilter);

        redirectAttr.addAttribute("id", lutaBinlogFilter.getId());

        return "redirect:/console/filter/detail";
    }
    @RequestMapping("/filter/detail")
    public String filterDetail(@RequestParam(required=true) Long id, Model model){
        LutaBinlogFilter lutaBinlogFilter = binlogService.getBinlogFilter(id);
        model.addAttribute("binlogFilter", lutaBinlogFilter);
        return "filterDetail";
    }
}
