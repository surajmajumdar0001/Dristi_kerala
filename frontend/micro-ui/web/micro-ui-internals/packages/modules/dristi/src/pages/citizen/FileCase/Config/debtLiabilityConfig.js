const debtLiabilityFromconfig = [
  {
    body: [
      {
        type: "dropdown",
        key: "liabilityNature",
        label: "CS_NATURE_DEBT_LIABILITY",
        isMandatory: true,
        populators: {
          label: "SELECT_RESPONDENT_TYPE",
          name: "liabilityNature",
          type: "radioButton",
          optionsKey: "name",
          error: "CORE_REQUIRED_FIELD_ERROR",
          required: false,
          isMandatory: true,
          clearFields: { stateOfRegistration: "", barRegistrationNumber: "", barCouncilId: [], stateRegnNumber: "" },
          options: [
            {
              code: "LOAN",
              name: "Loan",
              showAmountCovered: false,
              isEnabled: true,
            },
            {
              code: "SALES",
              name: "Sales Agreement",
              showAmountCovered: true,
              isVerified: true,
              hasBarRegistrationNo: true,
              isEnabled: true,
              apiDetails: {
                serviceName: "/advocate/advocate/v1/_create",
                requestKey: "advocates",
                AdditionalFields: ["barRegistrationNumber"],
              },
            },
          ],
        },
      },
    ],
  },
  {
    body: [
      {
        type: "radio",
        key: "liabilityType",
        label: "CS_CHEQUE_LIABILITY",
        isMandatory: true,
        populators: {
          label: "SELECT_RESPONDENT_TYPE",
          name: "liabilityType",
          type: "radioButton",
          optionsKey: "name",
          error: "CORE_REQUIRED_FIELD_ERROR",
          required: false,
          isMandatory: true,
          isDependent: true,
          clearFields: { stateOfRegistration: "", barRegistrationNumber: "", barCouncilId: [], stateRegnNumber: "" },
          options: [
            {
              code: "FULL_LIABILITY",
              name: "Full Liability",
              showAmountCovered: false,
              isEnabled: true,
            },
            {
              code: "PARTIAL_LIABILITY",
              name: "Partial Liability",
              showAmountCovered: true,
              isVerified: true,
              hasBarRegistrationNo: true,
              isEnabled: true,
              apiDetails: {
                serviceName: "/advocate/advocate/v1/_create",
                requestKey: "advocates",
                AdditionalFields: ["barRegistrationNumber"],
              },
            },
          ],
        },
      },
    ],
  },
  {
    dependentKey: { liabilityType: ["showAmountCovered"] },
    body: [
      {
        type: "amount",
        component: "CustomInput",
        label: "CS_TOTAL_CHEQUE_AMOUNT",
        isMandatory: true,
        populators: {
          componentInFront: "₹",
          name: "totalAmount",
          prefix: "",
          validation: {
            maxLength: 12,
            max: 999999999999,
          },
        },
      },
    ],
  },
  {
    body: [
      {
        type: "component",
        component: "SelectCustomNote",
        key: "addressDetailsNote",
        populators: {
          inputs: [
            {
              infoHeader: "CS_COMMON_NOTE",
              infoText: "CS_NOTE_DEBT_LIABILITY",
              infoTooltipMessage: "Tooltip",
              type: "InfoComponent",
            },
          ],
        },
      },
    ],
  },
  {
    body: [
      {
        type: "component",
        component: "SelectCustomDragDrop",
        key: "debtLiabilityFileUpload",
        populators: {
          inputs: [
            {
              name: "document",
              documentHeader: "CS_PROOF_DEBT",
              isOptional: "CS_IS_OPTIONAL",
              infoTooltipMessage: "Tooltip",
              type: "DragDropComponent",
              uploadGuidelines: "UPLOAD_DOC_50",
              maxFileSize: 50,
              maxFileErrorMessage: "CS_FILE_LIMIT_50_MB",
              fileTypes: ["JPG", "PDF"],
              isMultipleUpload: false,
            },
          ],
        },
      },
    ],
  },
  {
    body: [
      {
        type: "component",
        component: "SelectCustomTextArea",
        key: "additionalDebtLiabilityDetails",
        populators: {
          inputs: [
            {
              name: "text",
              textAreaSubHeader: "CS_DEBT_ADDITIONAL_DETAILS",
              isOptional: true,
              type: "TextAreaComponent",
            },
          ],
        },
      },
    ],
  },
];
export const debtliabilityconfig = {
  formconfig: debtLiabilityFromconfig,
  header: "CS_DEBT_LIABILITY_HEADING",
  subtext: "CS_DEBT_LIABILITY_SUBTEXT",
  isOptional: false,
  className: "debt-liability",
  selectDocumentName: {
    debtLiabilityFileUpload: "CS_PROOF_DEBT",
  },
};