package com.sdzee.tp.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sdzee.tp.beans.Client;
import com.sdzee.tp.dao.ClientDao;
import com.sdzee.tp.dao.DAOFactory;
import com.sdzee.tp.forms.CreationClientForm;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CreationClient")
public class CreationClient extends HttpServlet {
	public static final String CONF_DAO_FACTORY = "daofactory";
	public static final String CHEMIN = "chemin";
	public static final String ATT_CLIENT = "client";
	public static final String ATT_FORM = "form";
	public static final String SESSION_CLIENTS = "clients";

	public static final String VUE_SUCCES = "/WEB-INF/afficherClient.jsp";
	public static final String VUE_FORM = "/WEB-INF/creerClient.jsp";

	private static final long serialVersionUID = 1L;

	private ClientDao clientDao;

	public void init() throws ServletException {
		/* R?cup?ration d'une instance de notre DAO Utilisateur */
		this.clientDao = ((DAOFactory) getServletContext().getAttribute(CONF_DAO_FACTORY)).getClientDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* ? la r?ception d'une requ?te GET, simple affichage du formulaire */
		this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * Lecture du param?tre 'chemin' pass? ? la servlet via la d?claration dans le
		 * web.xml
		 */
		String chemin = this.getServletConfig().getInitParameter(CHEMIN);

		/* Pr?paration de l'objet formulaire */
		CreationClientForm form = new CreationClientForm(clientDao);

		/* Traitement de la requ?te et r?cup?ration du bean en r?sultant */
		Client client = form.creerClient(request, chemin);

		/* Ajout du bean et de l'objet m?tier ? l'objet requ?te */
		request.setAttribute(ATT_CLIENT, client);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {
			/* Alors r?cup?ration de la map des clients dans la session */
			HttpSession session = request.getSession();
			@SuppressWarnings("unchecked")
			Map<Long, Client> clients = (HashMap<Long, Client>) session.getAttribute(SESSION_CLIENTS);
			/* Si aucune map n'existe, alors initialisation d'une nouvelle map */
			if (clients == null) {
				clients = new HashMap<Long, Client>();
			}
			/* Puis ajout du client courant dans la map */
			clients.put(client.getId(), client);
			/* Et enfin (r?)enregistrement de la map en session */
			session.setAttribute(SESSION_CLIENTS, clients);

			/* Affichage de la fiche r?capitulative */
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);

		} else {
			/* Sinon, r?-affichage du formulaire de cr?ation avec les erreurs */
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}
	}

}
