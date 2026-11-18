const sidebars = {
  sidebar: [
    "index",
    "getting-started",
    {
      type: "category",
      label: "Reference",
      collapsed: false,
      items: [
        "reference/zlambda-runner",
        "reference/context",
        "event-and-response-types",
        "reference/events-api-gateway",
        "reference/events-messaging",
        "reference/events-cognito",
        "reference/events-other",
        "reference/responses"
      ]
    },
    "tutorials/http-api-on-api-gateway",
    {
      type: "category",
      label: "How-to Guides",
      collapsed: true,
      items: [
        "testing-locally",
        "deployment",
        "how-to/consume-an-sqs-batch",
        "how-to/decode-only-the-fields-you-need"
      ]
    }
  ]
};

module.exports = sidebars;
