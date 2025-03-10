import { Request } from "@egovernments/digit-ui-libraries";
import { Urls } from "../hooks";

export const DRISTIService = {
  postIndividualService: (data, tenantId) =>
    Request({
      url: Urls.dristi.individual,
      useCache: false,
      userService: false,
      data,
      params: { tenantId },
    }),
  updateAdvocateService: (data, params) =>
    Request({
      url: Urls.dristi.updateAdvocateDetails,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  searchIndividualUser: (data, params) =>
    Request({
      url: Urls.dristi.searchIndividual,
      useCache: false,
      userService: false,
      data,
      params,
    }),

  advocateClerkService: (url, data, tenantId, userService = false, additionInfo) =>
    Request({
      url: url,
      useCache: false,
      userService: userService,
      data,
      params: { tenantId, limit: 10000 },
      additionInfo,
    }),
  searchIndividualAdvocate: (data, params) =>
    Request({
      url: Urls.dristi.searchIndividualAdvocate,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  searchAdvocateClerk: (url, data, params) =>
    Request({
      url: url,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  caseCreateService: (data, tenantId) =>
    Request({
      url: Urls.dristi.caseCreate,
      useCache: false,
      userService: true,
      data,
      params: { tenantId },
    }),
  caseUpdateService: (data, tenantId) =>
    Request({
      url: Urls.dristi.caseUpdate,
      useCache: false,
      userService: true,
      data,
      params: { tenantId },
    }),
  searchCaseService: (data, params) =>
    Request({
      url: Urls.dristi.caseSearch,
      useCache: false,
      userService: false,
      data,
      params,
    }),
  updateEvidence: (data, params) =>
    Request({
      url: Urls.dristi.evidenceUpdate,
      useCache: false,
      userService: false,
      data,
      params,
    }),
  createEvidence: (data, params) =>
    Request({
      url: Urls.dristi.evidenceCreate,
      useCache: false,
      userService: false,
      data,
      params,
    }),
  searchEvidence: (data) => {
    return Request({
      url: Urls.dristi.evidenceSearch,
      useCache: false,
      userService: false,
      data,
    });
  },
  searchHearings: (data, params) => {
    return Request({
      url: Urls.dristi.searchHearings,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  createHearings: (data, params) => {
    return Request({
      url: Urls.dristi.createHearings,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  searchOrders: (data, params) => {
    return Request({
      url: Urls.dristi.ordersSearch,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  searchSubmissions: (data, params) => {
    return Request({
      url: Urls.dristi.submissionsSearch,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  updateSubmissions: (data, params) => {
    return Request({
      url: Urls.dristi.submissionsUpdate,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  createDemand: (data, params) =>
    Request({
      url: Urls.dristi.demandCreate,
      useCache: false,
      userService: false,
      data,
      params,
    }),
  eSignService: (data, params) => {
    return Request({
      url: Urls.dristi.eSign,
      useCache: false,
      userService: false,
      data,
      params,
    });
  },
  createDemand: (data, params) =>
    Request({
      url: Urls.dristi.demandCreate,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  getPaymentBreakup: (data, params) =>
    Request({
      url: Urls.dristi.paymentCalculator,
      useCache: false,
      userService: false,
      data,
      params,
    }),
  callFetchBill: (data, params) =>
    Request({
      url: Urls.dristi.fetchBill,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  callETreasury: (data, params) =>
    Request({
      url: Urls.dristi.eTreasury,
      useCache: false,
      userService: true,
      data,
      params,
    }),
  callSearchBill: (data, params) =>
    Request({
      url: Urls.dristi.searchBill,
      useCache: false,
      userService: true,
      data,
      params,
    }),
};
