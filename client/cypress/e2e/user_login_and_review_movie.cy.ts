/// <reference types="cypress" />

describe("User Login and reveiew interstellar ", () => {
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

    cy.contains("Interstellar").click();
    
    // Review the movie
    cy.get("textarea").type("Great movie about space and time!");
    cy.get('select').select('7');
    cy.contains('button', 'Add review').click();



  });
});

