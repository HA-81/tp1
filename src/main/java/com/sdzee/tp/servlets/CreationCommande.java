package com.sdzee.tp.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sdzee.tp.beans.Client;
import com.sdzee.tp.beans.Commande;
import com.sdzee.tp.dao.ClientDao;
import com.sdzee.tp.dao.CommandeDao;
import com.sdzee.tp.dao.DAOFactory;
import com.sdzee.tp.forms.CreationCommandeForm;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CreationCommande")
public class CreationCommande extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String CONF_DAO_FACTORY = "daofactory";
	public static final String CHEMIN = "chemin";
	public static final String ATT_COMMANDE = "commande";
	public static final String ATT_FORM = "form";
	public static final String SESSION_CLIENTS = "clients";
	public static final String SESSION_COMMANDES = "commandes";

	public static final String VUE_SUCCES = "/WEB-INF/afficherCommande.jsp";
	public static final String VUE_FORM = "/WEB-INF/creerCommande.jsp";

	private ClientDao clientDao;
	private CommandeDao commandeDao;

	public void init() throws ServletException {
		/* Récupération d'une instance de nos DAO Client et Commande */
		this.clientDao = ((DAOFactory) getServletContext().getAttribute(CONF_DAO_FACTORY)).getClientDao();
		this.commandeDao = ((DAOFactory) getServletContext().getAttribute(CONF_DAO_FACTORY)).getCommandeDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* À la réception d'une requête GET, simple affichage du formulaire */
		this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * Lecture du paramètre 'chemin' passé à la servlet via la déclaration dans le
		 * web.xml
		 */
		String chemin = this.getServletConfig().getInitParameter(CHEMIN);

		/* Préparation de l'objet formulaire */
		CreationCommandeForm form = new CreationCommandeForm(clientDao, commandeDao);

		/* Traitement de la requête et récupération du bean en résultant */
		Commande commande = form.creerCommande(request, chemin);

		/* Ajout du bean et de l'objet métier à l'objet requête */
		request.setAttribute(ATT_COMMANDE, commande);
		request.setAttribute(ATT_FORM, form);

		/* Si aucune erreur */
		if (form.getErreurs().isEmpty()) {
			/* Alors récupération de la map des clients dans la session */
			HttpSession session = request.getSession();
			Map<Long, Client> clients = (HashMap<Long, Client>) session.getAttribute(SESSION_CLIENTS);
			/* Si aucune map n'existe, alors initialisation d'une nouvelle map */
			if (clients == null) {
				clients = new HashMap<Long, Client>();
			}
			/* Puis ajout du client de la commande courante dans la map */
			clients.put(commande.getClient().getId(), commande.getClient());
			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_CLIENTS, clients);

			/* Ensuite récupération de la map des commandes dans la session */
			Map<Long, Commande> commandes = (HashMap<Long, Commande>) session.getAttribute(SESSION_COMMANDES);
			/* Si aucune map n'existe, alors initialisation d'une nouvelle map */
			if (commandes == null) {
				commandes = new HashMap<Long, Commande>();
			}
			/* Puis ajout de la commande courante dans la map */
			commandes.put(commande.getId(), commande);
			/* Et enfin (ré)enregistrement de la map en session */
			session.setAttribute(SESSION_COMMANDES, commandes);
			/* Affichage de la fiche récapitulative */
			this.getServletContext().getRequestDispatcher(VUE_SUCCES).forward(request, response);
		} else {
			/* Sinon, ré-affichage du formulaire de création avec les erreurs */
			this.getServletContext().getRequestDispatcher(VUE_FORM).forward(request, response);
		}
	}
}
