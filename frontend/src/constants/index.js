import {
  FiFileText,
  FiMic,
  FiTarget,
  FiTrendingUp,
  FiZap,
  FiShield,
  FiHome,
  FiBriefcase,
  FiUsers,
  FiBarChart2,
  FiSettings,
  FiActivity,
  FiCpu,
} from "react-icons/fi";

export const APP_NAME = "PlaceNextAI";

export const NAV_LINKS = [
  { label: "Features", href: "#features" },
  { label: "How it works", href: "#how-it-works" },
  { label: "For recruiters", href: "#recruiters" },
  { label: "Stats", href: "#stats" },
];

export const FEATURES = [
  {
    icon: FiFileText,
    title: "AI Resume Analyzer",
    description:
      "Deep semantic analysis of your resume against real job descriptions with an instant ATS score and fix suggestions.",
    accent: "from-primary-500 to-primary-600",
  },
  {
    icon: FiMic,
    title: "Mock Interview Agent",
    description:
      "Practice technical and HR rounds with an AI interviewer that adapts questions to your target role and rates every answer.",
    accent: "from-accent-500 to-accent-600",
  },
  {
    icon: FiTarget,
    title: "Smart Job Matching",
    description:
      "Sentence-transformer embeddings match your profile to openings by skill overlap, not just keyword counting.",
    accent: "from-primary-500 to-accent-500",
  },
  {
    icon: FiTrendingUp,
    title: "Skill Gap Roadmaps",
    description:
      "See exactly which skills separate you from your dream role and get a week-by-week learning plan to close the gap.",
    accent: "from-accent-400 to-primary-500",
  },
  {
    icon: FiZap,
    title: "AI Candidate Shortlisting",
    description:
      "Recruiters rank hundreds of applicants in seconds with explainable, bias-aware relevance scoring.",
    accent: "from-primary-600 to-accent-500",
  },
  {
    icon: FiShield,
    title: "Placement Analytics",
    description:
      "Live dashboards for readiness scores, application funnels and batch-level placement performance.",
    accent: "from-accent-500 to-primary-400",
  },
];

export const HOW_IT_WORKS = [
  {
    step: "01",
    title: "Build your profile",
    description:
      "Upload your resume and set your target roles. Our AI parses skills, projects and experience automatically.",
  },
  {
    step: "02",
    title: "Train with AI agents",
    description:
      "Run mock interviews, fix resume gaps and follow personalized skill roadmaps until your readiness score climbs.",
  },
  {
    step: "03",
    title: "Get matched & hired",
    description:
      "Recruiters see your verified readiness profile and AI ranks you into shortlists for roles you actually fit.",
  },
];

export const STATS = [
  { value: "94%", label: "Interview readiness lift" },
  { value: "12k+", label: "Mock interviews conducted" },
  { value: "3.5x", label: "Faster recruiter shortlisting" },
  { value: "150+", label: "Partner companies" },
];

export const STUDENT_NAV = [
  { label: "Overview", icon: FiHome, to: "/dashboard/student" },
  { label: "Resume Analyzer", icon: FiFileText, to: "/dashboard/student/resume" },
  { label: "Mock Interviews", icon: FiMic },
  { label: "Job Matches", icon: FiTarget },
  { label: "Skill Roadmap", icon: FiTrendingUp, to: "/dashboard/student/roadmap" },
  { label: "Settings", icon: FiSettings },
];

export const RECRUITER_NAV = [
  { label: "Overview", icon: FiHome, to: "/dashboard/recruiter" },
  { label: "Job Postings", icon: FiBriefcase },
  { label: "Candidates", icon: FiUsers },
  { label: "AI Shortlists", icon: FiZap },
  { label: "Analytics", icon: FiBarChart2 },
  { label: "Settings", icon: FiSettings },
];

export const ADMIN_NAV = [
  { label: "Overview", icon: FiHome, to: "/dashboard/admin" },
  { label: "Students", icon: FiUsers, to: "/dashboard/admin/students" },
  { label: "Recruiters", icon: FiBriefcase, to: "/dashboard/admin/recruiters" },
  { label: "AI Services", icon: FiCpu, to: "/dashboard/admin/ai-services" },
  { label: "System Health", icon: FiActivity, to: "/dashboard/admin/system-health" },
  { label: "Settings", icon: FiSettings, to: "/dashboard/admin/settings" },
];

export const FOOTER_LINKS = [
  {
    heading: "Product",
    links: ["Resume Analyzer", "Mock Interviews", "Job Matching", "Analytics"],
  },
  {
    heading: "Company",
    links: ["About", "Careers", "Blog", "Contact"],
  },
  {
    heading: "Resources",
    links: ["Documentation", "API Reference", "Placement Guide", "Support"],
  },
  {
    heading: "Legal",
    links: ["Privacy Policy", "Terms of Service", "Security"],
  },
];
