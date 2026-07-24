import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiSearch, FiBookOpen } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV, ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getSuccessStories, getSuccessStoryCompanies } from "../services/mentorService";

export default function SuccessStoriesPage() {
  const { user } = useAuth();
  const isAlumni = user?.role === "ROLE_ALUMNI";

  const [stories, setStories] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [search, setSearch] = useState("");
  const [company, setCompany] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async (filters) => {
    setLoading(true);
    setError("");
    try {
      const [storyData, companyData] = await Promise.all([
        getSuccessStories(filters),
        getSuccessStoryCompanies(),
      ]);
      setStories(storyData);
      setCompanies(companyData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load success stories");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load({});
  }, [load]);

  const handleFilter = (event) => {
    event.preventDefault();
    load({ search, company });
  };

  return (
    <DashboardLayout
      navItems={isAlumni ? ALUMNI_NAV : STUDENT_NAV}
      roleLabel={isAlumni ? "Alumni" : "Student"}
      title="Success Stories"
      userName={user?.name || "User"}
    >
      <form onSubmit={handleFilter} className="glass-card flex flex-wrap items-center gap-3 p-4">
        <div className="relative flex-1 min-w-[220px]">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={15} />
          <input
            type="text"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search by role or keyword…"
            className="input-glass pl-10"
          />
        </div>
        <select
          value={company}
          onChange={(event) => setCompany(event.target.value)}
          className="input-glass w-auto min-w-[160px]"
        >
          <option value="">All companies</option>
          {companies.map((name) => (
            <option key={name} value={name}>
              {name}
            </option>
          ))}
        </select>
        <button
          type="submit"
          className="rounded-xl bg-brand-gradient px-4 py-2.5 text-sm font-semibold text-slate-900 dark:text-white shadow-glow-sm"
        >
          Filter
        </button>
      </form>

      <div className="mt-5">
        {loading && (
          <div className="grid gap-4 sm:grid-cols-2">
            <SkeletonBlock className="h-48" />
            <SkeletonBlock className="h-48" />
          </div>
        )}

        {!loading && error && stories.length === 0 && (
          <div className="glass-card">
            <ErrorState message={error} onRetry={() => load({ search, company })} />
          </div>
        )}

        {!loading && !error && stories.length === 0 && (
          <div className="glass-card">
            <EmptyState
              icon={FiBookOpen}
              title="No success stories yet"
              message="Alumni interview experiences will appear here as they're shared."
            />
          </div>
        )}

        {!loading && stories.length > 0 && (
          <div className="grid gap-4 sm:grid-cols-2">
            {stories.map((story, index) => (
              <motion.div
                key={story.id}
                initial={{ opacity: 0, y: 14 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="glass-card p-5"
              >
                <p className="font-display text-base font-semibold text-slate-900 dark:text-white">{story.company}</p>
                <p className="text-xs text-slate-500">
                  {story.roleTitle} · shared by {story.alumniName}
                </p>
                <p className="mt-3 whitespace-pre-line text-sm leading-relaxed text-slate-500 dark:text-slate-400">{story.content}</p>
                <p className="mt-3 text-[11px] text-slate-600">{new Date(story.createdAt).toLocaleDateString()}</p>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
