const sidebars = {
  sidebar: [
    {
      type: "category",
      label: "ZIO Lambda",
      collapsed: false,
      link: { type: "doc", id: "index" },
      items: [
        "getting-started",
        "event-and-response-types",
        "testing-locally",
        "deployment"
      ]
    }
  ]
};

module.exports = sidebars;
