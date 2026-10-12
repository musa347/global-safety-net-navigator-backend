# 2-Minute Demo Script: Global Safety Net Navigator

**Goal:** Show the complete user journey and explain 2-3 key technical decisions that make this interesting.

---

## Setup (Before Recording)
- Open browser to: https://safetynetnavigator.vercel.app
- Have backend API URL ready: https://global-safety-net-navigator-backend.onrender.com
- Optional: Terminal ready for `curl` demo

---

## Script (2 minutes)

### [0:00-0:15] The Problem (15 seconds)

*"When someone loses income unexpectedly, insurance policy documents can be overwhelming. They need to know: Does my policy cover this? What do I need to verify? This tool tackles that problem."*

**[Show homepage]**

*"Global Safety Net Navigator is an information tool that helps users understand what published policy documents say about their income situation."*

---

### [0:15-0:45] User Journey (30 seconds)

**[Click 'Get Started']**

*"The user starts by selecting their scenario—in this case, 'My client cancelled a contract.'"*

**[Click scenario card]**

**[Result page loads]**

*"The system returns structured policy information, but notice three things:*

1. *Every policy condition shows its source—clicking opens the actual policy PDF*  
   **[Hover over 'Source: Policy Certificate']**

2. *All amounts include 'up to' qualifiers—never absolute guarantees*  
   **[Point to 'Up to $4,000']**

3. *The system explicitly says it's informational, not a decision*  
   **[Point to disclaimer banner]**

*That's not just UI design—it's architecturally enforced."*

---

### [0:45-1:15] Technical Approach (30 seconds)

**[Open browser DevTools Network tab, or switch to terminal]**

*"Let me show you what's happening under the hood."*

**[Execute curl or show Network request]**

```bash
curl -X POST https://.../api/v1/income/journeys \
  -H "Content-Type: application/json" \
  -d '{"scenarioId":"CONTRACT_CANCELLED"}'
```

*"The API response includes:*

- *Policy conditions with `sourceId` references*  
- *A complete sources array*  
- *A unique `requestId` for tracing*  
- *A `resultType` that's always `INFORMATIONAL`*  

*The backend validates source integrity: every policy condition must reference an existing source. If I add content without a valid source, the system won't return it."*

---

### [1:15-1:45] Key Decisions (30 seconds)

**[Optionally show architecture diagram or code]**

*"Three architectural decisions I want to highlight:*

**1. Source Integrity as Backend Concern**  
*The API contract guarantees source attribution. The frontend doesn't hope sources exist—they're structurally validated by the service layer before returning data.*

**2. In-Memory Storage (No Database)**  
*The MVP stores curated, static content. No user accounts, no claims, no transactions. A database would add infrastructure without solving an actual requirement. If versioning or editorial workflows emerge, I can swap to PostgreSQL easily—repositories are interface-based.*

**3. Testing Critical Behaviors**  
*I have 13 tests covering source integrity, API contract, and ensuring the system never generates eligibility language. That's 78% coverage, which is pragmatic—testing behaviors that matter, not chasing 100% for vanity."*

---

### [1:45-2:00] Wrap-up (15 seconds)

**[Return to result page]**

*"This demonstrates end-to-end product thinking: identifying a user problem, defining clear boundaries—information versus decision—choosing appropriate technology, and shipping working software with zero cost deployment."*

**[Show footer: 'Independent prototype']**

*"It's an educational prototype—not affiliated with SafetyWing—but it shows how I approach product engineering: solve the right problem, document decisions, and ship."*

---

## Alternative: 1-Minute Version (If Time Constrained)

### [0:00-0:10] Problem
*"Insurance policies are dense. This tool helps users understand what the published policy says about their income situation."*

### [0:10-0:30] User Journey
**[Show homepage → select scenario → result page]**  
*"User selects their scenario, sees structured policy information with source attribution, 'up to' qualifiers, and a clear disclaimer that this is informational, not a decision."*

### [0:30-0:50] Technical Highlight
**[Show API response or architecture]**  
*"The API validates source integrity—every policy condition references an existing source. Results are always marked INFORMATIONAL. Tests validate these invariants."*

### [0:50-1:00] Wrap-up
*"Zero-cost deployment, 13 passing tests, pragmatic architecture. This is how I approach product engineering: ship working software that solves the right problem."*

---

## Demo Tips

### What to Emphasize
- **Product boundary:** Information vs eligibility decision  
- **Source integrity:** Backend validation, not just UI  
- **Pragmatic testing:** 78% coverage with critical behaviors  
- **Architecture decisions:** In-memory storage justified by requirements  
- **Shipping velocity:** Deployed MVP in days, zero cost

### What to Avoid
- Don't read the UI text word-for-word  
- Don't get lost in code details (save for deep-dive questions)  
- Don't apologize for "only" 2 scenarios—frame as intentional scope  
- Don't spend >10 seconds on any one screen

### Backup Slides (If Asked)
- Architecture diagram (visual overview)
- Test suite output (`mvn test`)
- Code snippet: `SourceValidationService`
- Deployment pipeline (GitHub → Render/Vercel)

---

## Practice Runs

### Run 1: Time yourself
- Aim for 1:45-2:00 (leave buffer for questions)
- Identify sections where you're talking too fast/slow

### Run 2: Record video
- Watch for nervous habits (um, uh, you know)
- Check if screen is clearly visible

### Run 3: Technical validation
- Ensure backend isn't cold-started (15-30 sec delay on Render free tier)
- Pre-load frontend so no loading spinners during demo
- Test curl commands work from your machine

---

## Q&A Preparation

### "Why no database?"
> "The MVP stores curated, static content—no user accounts, claims, or transactions. A database would add infrastructure without solving an actual requirement. I used repository interfaces so swapping to PostgreSQL later is straightforward, but I didn't pay the infrastructure cost upfront."

### "How did you ensure reliability?"
> "I tested critical behaviors: source integrity, API contract validation, and ensuring the system never generates eligibility language. That's 78% coverage with 13 tests. I focused on protecting product boundaries rather than chasing 100% coverage on trivial code."

### "What would you add next?"
> "Content versioning metadata—showing when policy info was last reviewed. Then additional scenarios (medical leave, disability). If multiple people need to edit content, I'd add a database and editorial workflow. The architecture is set up to evolve as requirements grow."

### "How long did this take?"
> "About a week end-to-end: problem definition, policy research, domain modeling, implementation, testing, and deployment. The second scenario took 30 minutes because the architecture was proven. That's the value of doing one thing completely rather than many things partially."

---

## Equipment Checklist

- [ ] Browser open to https://safetynetnavigator.vercel.app
- [ ] Backend awake (hit health endpoint 30 sec before demo)
- [ ] Screen resolution set to 1920x1080 (or readable on small screens)
- [ ] Browser zoom at 100% (not 125% or 150%)
- [ ] Close unnecessary tabs/windows
- [ ] Disable notifications (Slack, email, etc.)
- [ ] Terminal ready if showing curl commands
- [ ] Architecture diagram open in separate window (backup)

---

## Post-Demo

### If They Want More
- Walk through architecture diagram
- Show test suite output
- Explain CORS fix (production debugging story)
- Discuss coverage threshold decision (75% vs 80%)

### If Time Is Short
- "I have documentation covering architecture, deployment, and case study—happy to share or answer specific questions later."

---

## Recording Notes (If Async)

### Tools
- **macOS:** QuickTime Player (Screen Recording)
- **Windows:** OBS Studio or PowerPoint (Export as Video)
- **Linux:** SimpleScreenRecorder

### Settings
- 1920x1080 resolution
- 30 FPS (sufficient for demo, not gaming)
- Include system audio if demoing API calls with sound
- Export as MP4 (H.264) for universal playback

### Editing (Optional)
- Add text overlay: "Global Safety Net Navigator" (title card)
- Speed up loading screens (1.5x) if backend cold-starts
- Add captions if presenting in non-native language

---

## Final Checklist

Before presenting:
- [ ] Frontend loads successfully
- [ ] Backend is warm (no cold start delay)
- [ ] Can navigate scenario → result page smoothly
- [ ] curl command works (if showing terminal)
- [ ] Timer set to 2:00 (or 1:00 if shorter version)
- [ ] Architecture diagram ready (backup)
- [ ] Practiced at least 3 times

---

## Summary

**Goal:** Show the complete user journey + explain 2-3 key technical decisions in 2 minutes.

**Structure:**
1. Problem (15 sec)
2. User journey (30 sec)
3. Technical approach (30 sec)
4. Key decisions (30 sec)
5. Wrap-up (15 sec)

**Emphasis:** Product thinking, architectural pragmatism, testing philosophy, shipping velocity.

**Outcome:** Viewer understands the problem you solved, how you solved it, and why your decisions were appropriate for the context.
