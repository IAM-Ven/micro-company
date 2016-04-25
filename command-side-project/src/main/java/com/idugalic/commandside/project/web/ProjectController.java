package com.idugalic.commandside.project.web;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.idugalic.commandside.project.command.CreateProjectCommand;
import com.idugalic.common.model.AuditEntry;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

	private String getCurrentUser() {
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			return SecurityContextHolder.getContext().getAuthentication().getName();
		}
		return null;
	}

	private AuditEntry createAudit() {
		return new AuditEntry(getCurrentUser());
	}

	@Autowired
	private CommandGateway commandGateway;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void create(@RequestBody CreateProjectRequest request, HttpServletResponse response, Principal principal) {
		LOG.debug(CreateProjectRequest.class.getSimpleName() + " request received");

		CreateProjectCommand command = new CreateProjectCommand(createAudit());
		commandGateway.sendAndWait(command);
		LOG.debug(CreateProjectCommand.class.getSimpleName() + " sent to command gateway: Blog Post [{}] ", command.getId());
	}

}
