package com.sdzee.tp.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sdzee.tp.beans.Commande;
import com.sdzee.tp.dao.CommandeDao;
import com.sdzee.tp.dao.DAOException;
import com.sdzee.tp.dao.DAOFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/SuppressionCommande")
public class SuppressionCommande extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String CONF_DAO_FACTORY = "daofactory";
	public static final String PARAM_ID_COMMANDE = "idCommande";
	public static final String SESSION_COMMANDES = "commandes";

	public static final String VUE = "/listeCommandes";

	private CommandeDao commandeDao;

	public void init() throws ServletException {
		/* Récupération d'une instance de notre DAO Utilisateur */
		this.commandeDao = ((DAOFactory) getServletContext().getAttribute(CONF_DAO_FACTORY)).getCommandeDao();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/* Récupération du paramètre */
		String idCommande = getValeurParametre(request, PARAM_ID_COMMANDE);
		Long id = Long.parseLong(idCommande);

		/* Récupération de la Map des commandes enregistrées en session */
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Map<Long, Commande> commandes = (HashMap<Long, Commande>) session.getAttribute(SESSION_COMMANDES);

		/* Si l'id de la commande et la Map des commandes ne sont pas vides */
		if (id != null && commandes != null) {
			try {
				/* Alors suppression de la commande de la BDD */
				commandeDao.supprimer(commandes.get(id));
				/* Puis suppression de la commande de la Map */
				commandes.remove(id);
			} catch (DAOException e) {
				e.printStackTrace();
			}

			/* Et remplacement de l'ancienne Map en session par la nouvelle */
			session.setAttribute(SESSION_COMMANDES, commandes);
		}

		/* Redirection vers la fiche récapitulative */
		response.sendRedirect(request.getContextPath() + VUE);
	}

	/*
	 * Méthode utilitaire qui retourne null si un paramètre est vide, et son contenu
	 * sinon.
	 */
	private static String getValeurParametre(HttpServletRequest request, String nomChamp) {
		String valeur = request.getParameter(nomChamp);
		if (valeur == null || valeur.trim().length() == 0) {
			return null;
		} else {
			return valeur;
		}
	}

}
