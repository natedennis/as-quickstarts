package org.jboss.as.quickstarts.kitchensinkrf.controller;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.as.quickstarts.kitchensinkrf.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensinkrf.model.Member;
import org.richfaces.cdi.push.Push;

// The @Model stereotype is a convenience mechanism to make this a request-scoped bean that has an
// EL name
// Read more about the @Model stereotype in this FAQ:
// http://sfwk.org/Documentation/WhatIsThePurposeOfTheModelAnnotation
//@Model
@Named
@Stateful
@ConversationScoped
public class MemberController {

   public static final String PUSH_CDI_TOPIC = "pushCdi";

   @Inject
   private FacesContext facesContext;

   @Inject
   private MemberRegistration memberRegistration;

   @Inject
   @Push(topic = PUSH_CDI_TOPIC) Event<String> pushEvent;

   private Member newMember;
   private Member member;

   @Produces
   @Named
   public Member getNewMember() {
      return newMember;
   }

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}		

		if (this.conversation.isTransient()) {
			this.conversation.begin();
		}
		this.id = id;
	}
	
	@Inject
	private Conversation conversation; 
   
	
    @Inject
    private EntityManager em;
	
	public Member retrieve(Long id) {

		//if (FacesContext.getCurrentInstance().isPostback()) {
		//	return;
		//}
		System.out.println("in retrieve");

//		if (this.conversation.isTransient()) {
//			this.conversation.begin();
//		}
		
		if (id == null) {
			this.member = new Member();
			System.out.println("id is null");
			return this.member;
			
		} else {
			System.out.println("id is: " + id);
			this.member = this.em.find(Member.class, id);
			return this.member;
		}
	
	}
	
   /*public void end(){
	   this.conversation.end();
   }
*/   
	
   @Produces
   @Named
   public Member getMember() {
      return member;
   }

   public void setMember(Member member) {
	/*	if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}		

		if (this.conversation.isTransient()) {
			this.conversation.begin();
		}
		*/
      this.member = member;
   }

   public void register() throws Exception {
      memberRegistration.register(newMember);
      facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registered!", "Registration successful"));
      pushEvent.fire(String.format("New member added: %s (id: %d)", newMember.getName(), newMember.getId()));
      initNewMember();
   }

   @PostConstruct
   public void initNewMember() {
      newMember = new Member();
   }
}
