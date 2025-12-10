/// <reference types="cypress" />

describe("Admin Login and delete movie", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should log in and log out successfully", () => {
    // Open login modal
    cy.contains("Login").click();

    // Select inside the modal ONLY
    cy.get(".chakra-modal__content-container").within(() => {
      cy.get("input").eq(0).type("EmailKolar");
      cy.get('input[type="password"]').type("ASB123...lol");
      cy.contains('button',"Sign in").click();
    });

    // Make sure modal closes
    cy.get(".chakra-modal__content-container").should("not.exist");

    // Verify login
    cy.contains("EmailKolar").should("be.visible");

    // Go to admin panel
    cy.contains("Go to admin").click();

    // Open modal
    cy.contains("Add movie").click();

    // Fill modal form
    cy.get(".chakra-modal__content-container").within(() => {
      cy.get('input').eq(0).type("Cypress Test Movie");                 // Title
      cy.get('input').eq(1).type("99999");                              // TMDB ID
      cy.get('input').eq(2).type("A movie created by Cypress.");        // Overview
      cy.get('input').eq(3).type("2025-01-01");                         // Release date
      cy.get('input').eq(4).type("120");                                // Runtime
      //cy.get('input').eq(5).type("");     // Poster URL

      cy.contains("button", "Create").click();
    });

    // Wait for modal to close + UI to update
    cy.get(".chakra-modal__content-container").should("not.exist");

    // Verify movie exists in list
    cy.contains("Cypress Test Movie", { timeout: 10000 }).should("be.visible");

    // Delete it
    cy.contains("Cypress Test Movie")
      .parent()                     // go to its movie row
      .within(() => {
        cy.get('[aria-label="Delete movie"]').click();
      });

    // Confirm it disappears
    cy.contains("Cypress Test Movie").should("not.exist");


  });
});

